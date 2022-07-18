package money.tegro.dex.source

import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.*
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.bigint.BigInt
import org.ton.bitstring.BitString
import org.ton.block.AddrStd
import org.ton.block.Block
import org.ton.block.ShardDescr
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

@Singleton
class LiveBlockSource(
    private val liteApi: LiteApi,
) {
    private val sink: Sinks.Many<Block> = Sinks.many().multicast().onBackpressureBuffer()

    fun asFlux() = sink.asFlux()

    @PostConstruct
    private fun setup() =
        Flux.interval(Duration.ofSeconds(2))
            .concatMap { mono { liteApi.getMasterchainInfo().last } }
            .distinctUntilChanged()
            .concatMap {
                mono {
                    try {
                        logger.debug("getting masterchain block no. {}", value("seqno", it.seqno))
                        liteApi.getBlock(it).toBlock()
                    } catch (e: Exception) {
                        logger.warn("failed to get masterchain block no. {}", value("seqno", it.seqno), e)
                        null
                    }
                }
            }
            .concatMap {
                it.extra.custom.value?.shard_hashes
                    ?.nodes()
                    .orEmpty()
                    .flatMap {
                        val workchain = BigInt(it.first.toByteArray()).toInt()
                        it.second.nodes().map { workchain to it }
                    }
                    .toFlux()
                    .concatMap {
                        mono {
                            val (workchain, descr) = it
                            logger.debug(
                                "getting shard {} block no. {}", kv("workchain", workchain), v("seqno", descr.seq_no)
                            )
                            liteApi.getBlock(getBlockId(workchain, descr)).toBlock()
                        }
                    }
                    .mergeWith(mono { it }) // Don't forget the original masterchain block
            }
            .subscribe {
                sink.emitNext(it, Sinks.EmitFailureHandler.FAIL_FAST) // TODO: Replace with something more robust
            }

    companion object : KLogging() {
        @JvmStatic
        fun getBlockId(workchain: Int, descr: ShardDescr) = TonNodeBlockIdExt(
            workchain = workchain,
            shard = descr.next_validator_shard,
            seqno = descr.seq_no.toInt(),
            root_hash = descr.root_hash,
            file_hash = descr.file_hash
        )

        val SYSTEM_ADDRESSES = listOf(
            AddrStd(-1, BitString.of("5555555555555555555555555555555555555555555555555555555555555555")),
            AddrStd(-1, BitString.of("3333333333333333333333333333333333333333333333333333333333333333")),
            AddrStd(-1, BitString.of("0000000000000000000000000000000000000000000000000000000000000000")),
        )
    }
}
