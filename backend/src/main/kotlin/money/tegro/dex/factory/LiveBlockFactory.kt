package money.tegro.dex.source

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.api.tonnode.Shard
import org.ton.api.tonnode.TonNodeBlockId
import org.ton.bigint.BigInt
import org.ton.block.Block
import org.ton.lite.client.LiteClient

@Factory
open class LiveBlockFactory(
    private val registry: MeterRegistry,
    private val liteClient: LiteClient
) {
    @OptIn(FlowPreview::class)
    @Singleton
    fun liveBlocks(): Flow<Block> =
        flow {
            while (currentCoroutineContext().isActive) {
                emit(liteClient.getLastBlockId()) // Masterchain blocks
            }
        }
            .distinctUntilChanged()
            .mapNotNull { liteClient.getBlock(it)?.let(::listOf) }
            .runningReduce { accumulator, value ->
                val lastMcShards = accumulator.first().extra.custom.value?.shard_hashes
                    ?.nodes()
                    .orEmpty()
                    .associate { BigInt(it.first.toByteArray()).toInt() to it.second.nodes().maxBy { it.seq_no } }

                value.first().extra.custom.value?.shard_hashes
                    ?.nodes()
                    .orEmpty()
                    .associate { BigInt(it.first.toByteArray()).toInt() to it.second.nodes().maxBy { it.seq_no } }
                    .flatMap { curr ->
                        (lastMcShards.getOrDefault(curr.key, curr.value).seq_no..curr.value.seq_no)
                            .map { TonNodeBlockId(curr.key, Shard.ID_ALL, it.toInt()) }
                    }
                    .mapNotNull { liteClient.lookupBlock(it)?.let { liteClient.getBlock(it) } }
                    .plus(value)
            }
            .flatMapConcat { it.asFlow() }
            .onEach {
                logger.debug(
                    "block {} {}",
                    kv("workchain", it.info.shard.workchain_id),
                    kv("seqno", it.info.seq_no)
                )
            }
            .shareIn(CoroutineScope(Dispatchers.Default), SharingStarted.Lazily, 100)


    companion object : KLogging()
}
