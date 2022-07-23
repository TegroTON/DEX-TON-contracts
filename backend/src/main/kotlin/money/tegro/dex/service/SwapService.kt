package money.tegro.dex.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.model.SwapModel
import money.tegro.dex.repository.PairRepository
import money.tegro.dex.repository.SwapRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.bigint.BigInt
import org.ton.block.*
import org.ton.tlb.loadTlb
import reactor.core.publisher.Flux

@Singleton
open class SwapService(
    private val liveTransactions: Flux<Transaction>,
    private val pairRepository: PairRepository,
    private val swapRepository: SwapRepository,
) {
    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        liveTransactions
            // We only care for an inbound message from exchange pairs to:
            // a) Their respective jetton wallet in order to transfer jettons to the user
            // b) The user directly, when swapping XXX->TON
            .concatMap { mono { it.in_msg.value } }
            .filterWhen {
                mono {
                    // If null, this mono emits nothing and we just filter this transaction out
                    (it.info as? IntMsgInfo)?.src as? AddrStd
                }
                    .flatMap { pairRepository.existsById(it) }
            }
            .concatMap {
                mono {
                    val info = it.info as IntMsgInfo

                    val pair = pairRepository.findById(info.src as AddrStd).awaitSingle()

                    (it.body.x ?: it.body.y)?.beginParse()?.let { body ->
                        when (val op = body.loadUInt(32)) {
                            OP_TRANSFER -> { // Jetton transfer, need to further parse the payload
                                val wallet = info.dest
                                require(wallet == pair.baseWallet || wallet == pair.quoteWallet) // sanity

                                body.skipBits(64) // Query id
                                val amount = body.loadTlb(Coins).amount.value
                                val destination = body.loadTlb(MsgAddress) as AddrStd // Just die if not addrstd
                                body.loadTlb(MsgAddress) // Skip the second one, it's equal to destination

                                // These are appended straight to the payload
                                body.skipBits(1)
                                body.loadTlb(Coins) // ton_amount
                                body.skipBits(1)

                                when (val innerOp = body.loadUInt(32)) {
                                    OP_SUCCESSFUL_SWAP -> {
                                        logger.debug(
                                            "{}: successful jetton swap of {} {} -> {}",
                                            kv("op", innerOp.toString(16)),
                                            kv("value", amount),
                                            kv("wallet", (wallet as? AddrStd)?.toSafeBounceable() ?: wallet),
                                            kv("address", destination.toSafeBounceable())
                                        )

                                        SwapModel(
                                            address = destination,
                                            pair = pair.address,
                                            // To reduce number of cross-referenced values, we actually record what token was transferred,
                                            // not from which wallet. This way even if wallet address changes for some reason, we still have
                                            // solid information about the exact pair (assumed to never change) and exact token
                                            token = if (wallet == pair.quoteWallet) pair.quote else pair.base,
                                            amount = amount,
                                        )
                                    }
                                    else -> {
                                        logger.warn("unsupported inner {}", kv("op", op.toString(16)))
                                        null
                                    }
                                }
                            }
                            OP_SUCCESSFUL_SWAP -> { // TON transfer, easy one
                                logger.debug(
                                    "{} - successful ton swap of {} to {}",
                                    kv("op", op.toString(16)),
                                    kv("value", info.value.coins.amount.value),
                                    kv("address", (info.dest as AddrStd).toSafeBounceable())
                                )

                                SwapModel(
                                    address = info.dest as AddrStd,
                                    pair = pair.address,
                                    token = pair.base, // TON is assumed to always be the base currency
                                    amount = info.value.coins.amount.value,
                                )
                            }
                            else -> {
                                logger.warn(
                                    "unknown {}, cannot determine if it is a swap or not",
                                    kv("op", op.toString(16))
                                )
                                null
                            }
                        }
                    }
                }
            }
            .subscribe {
                swapRepository.save(it).subscribe()
            }
    }

    companion object : KLogging() {
        val OP_TRANSFER = BigInt(0xf8a7ea5);
        val OP_SUCCESSFUL_SWAP = BigInt(0xde6e0675);
    }
}
