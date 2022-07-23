package money.tegro.dex.source

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.bigint.BigInt
import org.ton.block.Block
import org.ton.block.ShardDescr
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

@Factory
class LiveBlockFactory {
    @Singleton
    fun liveBlocks(registry: MeterRegistry, liteApi: LiteApi): Flux<Block> =
        Flux.interval(Duration.ofSeconds(1))
            .concatMap { mono { liteApi.getMasterchainInfo().last } }
            .distinctUntilChanged()
            .concatMap {
                mono {
                    try {
                        logger.debug("getting masterchain block no. {}", v("seqno", it.seqno))
                        liteApi.getBlock(it).toBlock()
                    } catch (e: Exception) {
                        registry.counter("live.block.masterchain.failed").increment()

                        logger.warn("failed to get masterchain block no. {}", v("seqno", it.seqno), e)
                        null
                    }
                }
            }
            .timed()
            .doOnNext {
                registry.timer("live.block.masterchain")
                    .record(it.elapsed())
            }
            .concatMap {
                it.get().extra.custom.value?.shard_hashes
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
                    .mergeWith(mono { it.get() }) // Don't forget the original masterchain block
            }
            .publish()
            .autoConnect()

    companion object : KLogging() {
        @JvmStatic
        fun getBlockId(workchain: Int, descr: ShardDescr) = TonNodeBlockIdExt(
            workchain = workchain,
            shard = descr.next_validator_shard,
            seqno = descr.seq_no.toInt(),
            root_hash = descr.root_hash,
            file_hash = descr.file_hash
        )
    }
}
