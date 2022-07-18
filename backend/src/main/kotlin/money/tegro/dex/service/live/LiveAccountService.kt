package money.tegro.dex.service.live

import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.service.live.LiveBlockService.Companion.SYSTEM_ADDRESSES
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.block.AddrStd
import reactor.core.publisher.Sinks

@Singleton
class LiveAccountService(
    private val blockService: LiveBlockService,
) {
    private val sink: Sinks.Many<AddrStd> = Sinks.many().multicast().onBackpressureBuffer()

    fun asFlux() = sink.asFlux()

    @PostConstruct
    private fun setup() =
        blockService.asFlux()
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
