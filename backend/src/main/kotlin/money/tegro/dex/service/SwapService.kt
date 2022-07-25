package money.tegro.dex.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import money.tegro.dex.contract.OpTransfer
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.model.SwapModel
import money.tegro.dex.repository.PairRepository
import money.tegro.dex.repository.SwapRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.IntMsgInfo
import org.ton.block.Transaction
import org.ton.tlb.loadTlb
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Singleton
open class SwapService(
    private val liveTransactions: Flux<Transaction>,
    private val pairRepository: PairRepository,
    private val swapRepository: SwapRepository,
) {
    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        // Classic T?.toMono() -> Mono<T> skip is used extensively here to deal with ?????-hell
        // Simply put, it allows to simply finish processing the entire sequence if some value is null


        // We only care for an inbound message from exchange pairs to:
        // a) The user directly, when swapping XXX->TON
        // b) Their respective jetton wallet in order to transfer jettons to the user
        liveTransactions
            .concatMap { transaction ->
                transaction.in_msg.value.toMono()
                    .filterWhen {
                        (it.body.x ?: it.body.y).toMono()
                            .map {
                                val bs = it.beginParse()
                                bs.bitsPosition + 32 <= bs.bits.size// We can read 32 bits
                                        && bs.loadUInt(32) == OP_SUCCESSFUL_SWAP // It's a simple XXX->TON swap
                            }
                    }
                    .flatMap { (it.info as? IntMsgInfo).toMono() }
                    .flatMap { info ->
                        Mono.zip(
                            (info.dest as? AddrStd).toMono(), // Destination is addrstd, otherwise just empty
                            info.value.coins.amount.value.toMono(),
                            (info.src as? AddrStd).toMono() // If not addrstd, just skips everything
                                .flatMap { pairRepository.findById(it) }, // Transaction from the main pair contract
                            (info.created_lt).toMono(),
                        )
                    }
            }
            .subscribe {
                logger.debug(
                    "successful ton swap by {} of {} on {} at {}",
                    kv("address", it.t1.toSafeBounceable()),
                    kv("value", it.t2),
                    kv("pair", it.t3.address.toSafeBounceable()),
                    kv("lt", it.t4)
                )

                swapRepository.save(
                    SwapModel(
                        address = it.t1,
                        amount = it.t2,
                        pair = it.t3.address,
                        token = it.t3.base, // TON is assumed to always be the base currency
                        lt = it.t4,
                    )
                ).subscribe()
            }

        // Same but for XXX->Jetton swaps. A bit more complex
        liveTransactions
            .concatMap { transaction ->
                transaction.in_msg.value.toMono()
                    .filterWhen {
                        (it.body.x ?: it.body.y).toMono()
                            .map {
                                val bs = it.beginParse()
                                bs.bitsPosition + 32 <= bs.bits.size// We can read 32 bits
                                        && bs.loadUInt(32) == OP_TRANSFER // Token transfer
                            }
                    }
                    .flatMap {
                        val info = it.info as? IntMsgInfo
                        Mono.zip(
                            info.toMono(),
                            (it.body.x ?: it.body.y)?.parse { loadTlb(OpTransfer) }.toMono()
                                .filterWhen {
                                    (it.forward_payload.x ?: it.forward_payload.y).toMono()
                                        .map {
                                            val bs = it.beginParse()
                                            bs.bitsPosition + 32 <= bs.bits.size// We can read 32 bits
                                                    && bs.loadUInt(32) == OP_SUCCESSFUL_SWAP // Success!
                                        }
                                },
                            (info?.src as? AddrStd).toMono() // If not addrstd, just skips everything
                                .flatMap { pairRepository.findById(it) }, // Transaction from the main pair contract
                            (info?.created_lt).toMono(),
                        )
                    }
            }
            .subscribe {
                val info = it.t1
                val transfer = it.t2
                val pair = it.t3
                val lt = it.t4

                (transfer.destination as? AddrStd)?.let { destination ->
                    logger.debug(
                        "successful jetton swap of {} by {} -> {} at {}",
                        kv("value", transfer.amount.value),
                        kv("wallet", (info.dest as? AddrStd)?.toSafeBounceable() ?: info.dest),
                        kv("address", destination.toSafeBounceable()),
                        kv("lt", lt)
                    )

                    swapRepository.save(
                        SwapModel(
                            address = destination,
                            amount = transfer.amount.value,
                            pair = pair.address,
                            // To reduce number of cross-referenced values, we actually record what token was transferred,
                            // not from which wallet. This way even if wallet address changes for some reason, we still have
                            // solid information about the exact pair (assumed to never change) and exact token
                            token = if (info.dest == pair.quoteWallet) pair.quote else pair.base,
                            lt = it.t4,
                        )
                    ).subscribe()
                }
            }
    }

    companion object : KLogging() {
        val OP_TRANSFER = BigInt(0xf8a7ea5);
        val OP_SUCCESSFUL_SWAP = BigInt(0xde6e0675);
    }
}
