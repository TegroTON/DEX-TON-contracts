package money.tegro.dex.source

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.api.tonnode.Shard
import org.ton.api.tonnode.TonNodeBlockId
import org.ton.bigint.BigInt
import org.ton.block.Block
import org.ton.lite.client.LiteClient
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@Factory
open class LiveBlockFactory(
    private val registry: MeterRegistry,
    private val liteClient: LiteClient
) {
    @OptIn(ExperimentalTime::class)
    @Singleton
    fun liveBlocks(): Flow<Block> = flow {
        val workchains = liteClient.getLastBlockId().let {
            liteClient.getBlock(it)?.extra?.custom?.value?.shard_hashes
                ?.nodes()
                .orEmpty()
                .map { BigInt(it.first.toByteArray()).toInt() to it.second.nodes().maxOf { it.seq_no } }
                .plus(it.workchain to it.seqno.toLong())
                .toMap()
                .toMutableMap()
        }
        while (currentCoroutineContext().isActive) {
            workchains.forEach { (wc, seqno) ->
                liteClient.lookupBlock(
                    TonNodeBlockId(wc, Shard.ID_ALL, seqno.toInt()), // TODO: Set to shard.id_all when on testnet
                    5.seconds
                )
                    ?.let { emit(it) }
                workchains.set(wc, seqno + 1)
            }
        }
    }
        .distinctUntilChanged()
        .mapNotNull { liteClient.getBlock(it) }
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
