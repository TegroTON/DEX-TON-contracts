package money.tegro.dex.source

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.config.FactoryConfig
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.api.tonnode.TonNodeBlockId
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.bigint.BigInt
import org.ton.block.Block
import org.ton.block.ShardDescr
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

@Factory
class LiveBlockFactory(
    private val config: FactoryConfig,
    private val registry: MeterRegistry,
    private val liteApi: LiteApi
) {
    private val liveMasterchainBlocks: Flux<Block> =
        Flux.interval(Duration.ZERO, config.liveBlockPeriod)
            .concatMap { mono { listOf(liteApi.getMasterchainInfo().last.seqno) } }
            .distinctUntilChanged()
            .scan { previous, current ->
                (previous.max() + 1 until current.min()) // All missing seqnos
                    .toList()
                    .plus(current)
            }
            .flatMapIterable { it }
            .concatMap {
                mono {
                    try {
                        logger.debug("getting masterchain block no. {}", v("seqno", it))
                        liteApi.lookupBlock(TonNodeBlockId(-1, BigInt("8000000000000000").toLong(), it))
                            .let { liteApi.getBlock(it.id) }
                            .toBlock()
                    } catch (e: Exception) {
                        registry.counter("live.block.masterchain.failed").increment()

                        logger.warn("failed to get masterchain block no. {}", v("seqno", it), e)
                        null
                    }
                }
                    .timed()
                    .map {
                        registry.timer("live.block.masterchain.elapsed")
                            .record(it.elapsed())
                        it.get()
                    }
            }
            .publish()
            .autoConnect()

    private val liveShardchainBlocks: Flux<Block> =
        liveMasterchainBlocks
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
                    .concatWithValues(it) // Don't forget the original masterchain block
            }
            .publish()
            .autoConnect()

    @Singleton
    fun liveBlocks(): Flux<Block> =
        Flux.merge(
            liveMasterchainBlocks,
            liveShardchainBlocks
        )
            .publish()
            .autoConnect()

    companion object : KLogging() {
        const val MASTERCHAIN_WORKCHAIN = -1
        val MASTERCHAIN_SHARD = BigInt("8000000000000000").toLong()

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
