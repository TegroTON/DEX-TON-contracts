package money.tegro.dex.factory

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import money.tegro.dex.contract.toSafeBounceable
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.bitstring.BitString
import org.ton.block.AddrStd
import org.ton.block.Block
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux

@Factory
class LiveAccountFactory {
    @Singleton
    fun liveAccounts(blocks: Flux<Block>, registry: MeterRegistry): Flux<AddrStd> =
        blocks
            .concatMap { block ->
                block.extra.account_blocks.nodes()
                    .flatMap {
                        sequenceOf(AddrStd(block.info.shard.workchain_id, it.first.account_addr))
                            .plus(it.first.transactions.nodes().map {
                                AddrStd(block.info.shard.workchain_id, it.first.account_addr)
                            })
                            .distinct()
                    }
                    .toFlux()
            }
            .filter { it !in SYSTEM_ADDRESSES }
            .doOnNext {
                registry.counter("source.live.account.affected").increment()

                logger.debug("affected account {}", v("address", it.toSafeBounceable()))
            }
            .publish()
            .autoConnect()

    companion object : KLogging() {
        val SYSTEM_ADDRESSES = listOf(
            AddrStd(-1, BitString.of("5555555555555555555555555555555555555555555555555555555555555555")),
            AddrStd(-1, BitString.of("3333333333333333333333333333333333333333333333333333333333333333")),
            AddrStd(-1, BitString.of("0000000000000000000000000000000000000000000000000000000000000000")),
        )
    }
}
