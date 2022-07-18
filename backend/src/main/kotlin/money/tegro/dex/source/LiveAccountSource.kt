package money.tegro.dex.source

import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.source.LiveBlockSource.Companion.SYSTEM_ADDRESSES
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.block.AddrStd
import reactor.core.publisher.Sinks

@Singleton
class LiveAccountSource(
    private val blockSource: LiveBlockSource,
) {
    private val sink: Sinks.Many<AddrStd> = Sinks.many().multicast().onBackpressureBuffer()

    fun asFlux() = sink.asFlux()

    @PostConstruct
    private fun setup() =
        blockSource.asFlux()
            .subscribe { block ->
                mono {// Processing each block asynchronously
                    block.extra.account_blocks.nodes()
                        .flatMap {
                            sequenceOf(AddrStd(block.info.shard.workchain_id, it.first.account_addr))
                                .plus(it.first.transactions.nodes().map {
                                    AddrStd(block.info.shard.workchain_id, it.first.account_addr)
                                })
                                .distinct()
                        }
                        .filter { it !in SYSTEM_ADDRESSES }
                        .forEach {
                            logger.debug("affected account {}", v("address", it.toSafeBounceable()))
                            sink.emitNext(it, Sinks.EmitFailureHandler.FAIL_FAST) // TODO: more robust handler
                        }
                }.subscribe()
            }

    companion object : KLogging()
}
