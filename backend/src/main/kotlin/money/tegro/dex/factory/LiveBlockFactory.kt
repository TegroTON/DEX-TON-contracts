package money.tegro.dex.source

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.config.FactoryConfig
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.api.tonnode.TonNodeBlockId
import org.ton.bigint.BigInt
import org.ton.block.Block
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.kotlin.extra.bool.not
import java.time.Duration
import java.time.Instant

@Factory
open class LiveBlockFactory(
    private val config: FactoryConfig,
    private val registry: MeterRegistry,
    private val liteApi: LiteApi
) {
    private val startedOn: Long = Instant.now().epochSecond // TODO: Logic Time instead? Doesn't seem to matter much

    private val blocksToProcess = Sinks.many().replay().limit<TonNodeBlockId>(Duration.ofMinutes(10))
    private val completeBlocks = Sinks.many().replay().limit<Block>(Duration.ofMinutes(10))

    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        // This is how we process blocks
        blocksToProcess
            .asFlux()
            .filterWhen { new ->
                completeBlocks.asFlux()
                    .timeout(Duration.ofSeconds(1), Mono.empty()) // Give it a sec or two before abandoning
                    .filter { // Find it
                        it.info.shard.workchain_id == new.workchain
                                && it.info.shard.shard_prefix == new.shard
                                && it.info.seq_no == new.seqno
                    }
                    .hasElements()
                    .not() // Continue if not found
            }
            .concatMap {
                mono {
                    try {
                        logger.debug("getting block {}", kv("id", it))
                        liteApi.lookupBlock(it).let { liteApi.getBlock(it.id) }.toBlock()
                    } catch (e: Exception) {
                        registry.counter(
                            "live.block.failed",
                            "workchain", it.workchain.toString(),
                            "shard", it.shard.toString()
                        ).increment()

                        logger.warn("failed to get block {}", kv("id", it), e)
                        null
                    }
                }
                    .timed()
                    .map {
                        registry.timer(
                            "live.block.elapsed",
                            "workchain", it.get().info.shard.workchain_id.toString(),
                            "shard", it.get().info.shard.shard_prefix.toString()
                        )
                            .record(it.elapsed())
                        it.get()
                    }
            }
            .doOnNext { block ->
                // Masterchain blocks also have shard_hashes, add their respective block ids to the queue
                block.extra.custom.value?.shard_hashes
                    ?.nodes()
                    .orEmpty()
                    .flatMap {
                        val workchain = BigInt(it.first.toByteArray()).toInt()
                        it.second.nodes().map { workchain to it }
                    }
                    .forEach {
                        blocksToProcess.emitNext(
                            TonNodeBlockId(
                                it.first,
                                it.second.next_validator_shard,
                                it.second.seq_no.toInt() // TODO: toInt()?
                            ),
                            Sinks.EmitFailureHandler.FAIL_FAST // TODO: more robust
                        )
                    }
            }
            .subscribe {
                completeBlocks.emitNext(
                    it,
                    Sinks.EmitFailureHandler.FAIL_FAST // TODO: more robust
                )
            }

        // Query for the last masterchain blocks
        Flux.interval(Duration.ZERO, Duration.ofSeconds(30))
            .concatMap { mono { liteApi.getMasterchainInfo().last } }
            .distinctUntilChanged()
            .subscribe {
                blocksToProcess.emitNext(
                    TonNodeBlockId(it.workchain, it.shard, it.seqno),
                    Sinks.EmitFailureHandler.FAIL_FAST // TODO: more robust
                )
            }

        // Make sure we didn't skip any masterchain blocks
        completeBlocks
            .asFlux()
            .filter { it.info.shard.workchain_id == -1 }
            .doOnNext { println(it.info.end_lt) }
            .takeUntil { it.info.gen_utime >= startedOn }
            .filterWhen { new ->
                completeBlocks.asFlux()
                    .timeout(Duration.ofSeconds(1), Mono.empty()) // Give it a sec or two before abandoning
                    .filter { // Find previous block
                        it.info.shard.workchain_id == new.info.shard.workchain_id
                                && it.info.shard.shard_prefix == new.info.shard.shard_prefix
                                && it.info.seq_no == new.info.seq_no - 1
                    }
                    .hasElements()
                    .not() // Continue if not found
            }
            .subscribe {
                blocksToProcess.emitNext(
                    TonNodeBlockId(it.info.shard.workchain_id, it.info.shard.shard_prefix, it.info.seq_no - 1),
                    Sinks.EmitFailureHandler.FAIL_FAST // TODO: more robust
                )
            }
    }

    @Prototype
    fun liveBlocks(): Flux<Block> = completeBlocks.asFlux()

    companion object : KLogging()
}
