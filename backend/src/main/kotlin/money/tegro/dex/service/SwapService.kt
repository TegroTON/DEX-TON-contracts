package money.tegro.dex.service

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import money.tegro.dex.contract.op.Op
import money.tegro.dex.contract.op.OpSuccessfulSwap
import money.tegro.dex.contract.op.OpTransfer
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.model.SwapModel
import money.tegro.dex.repository.PairRepository
import money.tegro.dex.repository.SwapRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.block.IntMsgInfo
import org.ton.block.MsgAddressInt
import org.ton.block.Transaction
import org.ton.tlb.parse

@Singleton
class SwapService(
    private val liveTransactions: Flow<Transaction>,
    private val pairRepository: PairRepository,
    private val swapRepository: SwapRepository,
) {
    @Scheduled(initialDelay = "0s")
    fun setup() {
        runBlocking(Dispatchers.Default) {
            launch { run() }
        }
    }

    private suspend fun run() {
        liveTransactions
            .filter { ((it.in_msg.value?.info as? IntMsgInfo)?.src as? AddrStd)?.let { pairRepository.existsById(it) } == true }
            .mapNotNull { transaction ->
                transaction.in_msg.value?.let { in_msg ->
                    (in_msg.info as? IntMsgInfo)?.let { info ->
                        (in_msg.body.x ?: in_msg.body.y)?.parse(Op)?.let { body ->
                            when (body) {
                                is OpSuccessfulSwap -> { // Simply in tons, XXX->TON swap
                                    SwapModel(
                                        hash = transaction.hash(),
                                        lt = transaction.lt,
                                        src = info.src, // For tons pair address is the source address
                                        dest = info.dest, // Just a simple transaction to user's wallet
                                        amount = info.value.coins.amount.value,
                                    )
                                }
                                is OpTransfer -> { // XXX->XXX swap
                                    (body.forward_payload.x ?: body.forward_payload.y)?.parse(Op)?.let { inner ->
                                        when (inner) {
                                            is OpSuccessfulSwap -> {
                                                (body.destination as? MsgAddressInt)?.let { dest ->
                                                    SwapModel(
                                                        hash = transaction.hash(),
                                                        lt = transaction.lt,
                                                        src = info.dest, // Fucky-wucky semantics here, simply put:
                                                        // Pair (info.src) -> It's jetton wallet (info.dest) -> User's address (body.destination)
                                                        dest = dest, // Users address
                                                        amount = body.amount.value,
                                                    )
                                                }
                                            }
                                            else -> {
                                                logger.warn("unknown inner op {}", inner)
                                                null
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .collect {
                logger.debug(
                    "successful at {} swap {} -> ({}) -> {}",
                    kv("lt", it.lt),
                    kv("src", (it.src as? AddrStd)?.toSafeBounceable() ?: it.dest),
                    kv("amount", it.amount),
                    kv("dest", (it.dest as? AddrStd)?.toSafeBounceable() ?: it.dest),
                )

                swapRepository.save(it)
            }
    }

    companion object : KLogging()
}
