package money.tegro.dex.factory

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import money.tegro.dex.contract.toSafeBounceable
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.bitstring.BitString
import org.ton.block.AddrStd
import org.ton.block.Block

@Factory
class LiveAccountFactory {
    @OptIn(FlowPreview::class)
    @Singleton
    fun liveAccounts(blocks: Flow<Block>, registry: MeterRegistry): Flow<AddrStd> =
        blocks
            .flatMapConcat { block ->
                block.extra.account_blocks.nodes()
                    .flatMap {
                        sequenceOf(AddrStd(block.info.shard.workchain_id, it.first.account_addr))
                            .plus(it.first.transactions.nodes().map {
                                AddrStd(block.info.shard.workchain_id, it.first.account_addr)
                            })
                            .distinct()
                    }
                    .asFlow()
            }
            .filter { it !in SYSTEM_ADDRESSES }
            .onEach {
                registry.counter("live.account.affected").increment()

                logger.debug("affected account {}", v("address", it.toSafeBounceable()))
            }
            .shareIn(CoroutineScope(Dispatchers.Default), SharingStarted.Lazily, 100)

    companion object : KLogging() {
        val SYSTEM_ADDRESSES = listOf(
            AddrStd(-1, BitString.of("5555555555555555555555555555555555555555555555555555555555555555")),
            AddrStd(-1, BitString.of("3333333333333333333333333333333333333333333333333333333333333333")),
            AddrStd(-1, BitString.of("0000000000000000000000000000000000000000000000000000000000000000")),
        )
    }
}
