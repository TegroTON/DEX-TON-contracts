package money.tegro.dex.service.live

import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.service.live.LiveBlockService.Companion.SYSTEM_ADDRESSES
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.block.AddrStd
import org.ton.block.IntMsgInfo
import org.ton.block.Transaction
import reactor.core.publisher.Sinks

@Singleton
class LiveTransactionService(
    private val blockService: LiveBlockService,
) {
    private val sink: Sinks.Many<Transaction> = Sinks.many().multicast().onBackpressureBuffer()

    fun asFlux() = sink.asFlux()

    @PostConstruct
    private fun setup() =
        blockService.asFlux()
            .subscribe { block ->
                mono {// Processing each block asynchronously
                    block.extra.account_blocks.nodes()
                        .flatMap {
                            it.first.transactions.nodes()
                                .map {
                                    block.info.shard.workchain_id to it.first
                                }
                        }
                        .filter { AddrStd(it.first, it.second.account_addr) !in SYSTEM_ADDRESSES }
                        .forEach {
                            run {
                                val (workchain, transaction) = it

                                logger.debug(
                                    "{} -- (in) -> {}",
                                    v(
                                        "srcAddress",
                                        ((transaction.in_msg.value?.info as? IntMsgInfo)?.src as? AddrStd)?.toSafeBounceable()
                                            ?: transaction.in_msg.value?.info
                                    ),
                                    v(
                                        "destAddress",
                                        ((transaction.in_msg.value?.info as? IntMsgInfo)?.dest as? AddrStd)?.toSafeBounceable()
                                            ?: transaction.in_msg.value?.info
                                    )
                                )
                                transaction.out_msgs.nodes()
                                    .forEach {
                                        logger.debug(
                                            "{} -- (out) -> {}",
                                            v(
                                                "srcAddress",
                                                ((it.second.info as? IntMsgInfo)?.src as? AddrStd)?.toSafeBounceable()
                                                    ?: it.second.info
                                            ),
                                            v(
                                                "destAddress",
                                                ((it.second.info as? IntMsgInfo)?.dest as? AddrStd)?.toSafeBounceable()
                                                    ?: it.second.info
                                            )
                                        )
                                    }
                            }

                            sink.emitNext(it.second, Sinks.EmitFailureHandler.FAIL_FAST) // TODO: more robust handler
                        }
                }.subscribe()
            }

    companion object : KLogging()
}
