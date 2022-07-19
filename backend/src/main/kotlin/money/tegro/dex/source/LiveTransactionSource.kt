package money.tegro.dex.source

import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.source.LiveBlockSource.Companion.SYSTEM_ADDRESSES
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.block.IntMsgInfo
import org.ton.block.Transaction
import reactor.core.publisher.Sinks
import reactor.kotlin.core.publisher.toFlux

@Singleton
class LiveTransactionSource(
    private val blockSource: LiveBlockSource,
) {
    private val sink: Sinks.Many<Transaction> = Sinks.many().multicast().onBackpressureBuffer()

    fun asFlux() = sink.asFlux()

    @PostConstruct
    private fun setup() =
        blockSource.asFlux()
            .concatMap { block ->
                block.extra.account_blocks.nodes()
                    .flatMap { it.first.transactions.nodes().map { it.first } }
                    .toFlux()
            }
            .filter {
                // Only transactions to regular non-system addresses
                ((it.in_msg.value?.info as? IntMsgInfo)?.dest as? AddrStd)?.let { it !in SYSTEM_ADDRESSES } ?: false
            }
            .doOnNext {
                (it.in_msg.value?.info as? IntMsgInfo)?.let { info ->
                    logger.debug(
                        "{} --(in)-> {}",
                        kv("src", (info.src as? AddrStd)?.toSafeBounceable() ?: info.src),
                        kv("dest", (info.dest as? AddrStd)?.toSafeBounceable() ?: info.dest)
                    )
                }

                it.out_msgs.toMap()
                    .values
                    .forEach {
                        (it.info as? IntMsgInfo)?.let { info ->
                            logger.debug(
                                "{} --(out)-> {}",
                                kv("src", (info.src as? AddrStd)?.toSafeBounceable() ?: info.src),
                                kv("dest", (info.dest as? AddrStd)?.toSafeBounceable() ?: info.dest)
                            )
                        }
                    }
            }
            .subscribe {
                sink.emitNext(it, Sinks.EmitFailureHandler.FAIL_FAST) // TODO: more robust handler
            }

    companion object : KLogging()
}
