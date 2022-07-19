package money.tegro.dex.source

import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.source.LiveBlockSource.Companion.SYSTEM_ADDRESSES
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.block.AddrStd
import reactor.core.publisher.Sinks
import reactor.kotlin.core.publisher.toFlux

@Singleton
class LiveAccountSource(
    private val blockSource: LiveBlockSource,
) {
    private val sink: Sinks.Many<AddrStd> = Sinks.many().multicast().onBackpressureBuffer()

    fun asFlux() = sink.asFlux()

    @PostConstruct
    private fun setup() =
        blockSource.asFlux()
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
            .subscribe {
                logger.debug("affected account {}", v("address", it.toSafeBounceable()))
                sink.emitNext(it, Sinks.EmitFailureHandler.FAIL_FAST) // TODO: more robust handler
            }

    companion object : KLogging()
}
