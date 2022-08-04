package money.tegro.dex.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.factory.LiveAccountFactory.Companion.SYSTEM_ADDRESSES
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.block.Block
import org.ton.block.IntMsgInfo
import org.ton.block.Transaction

@Factory
class LiveTransactionFactory {
    @Singleton
    fun liveTransactions(blocks: Flow<Block>): Flow<Transaction> =
        blocks
            .flatMapConcat { block ->
                block.extra.account_blocks.nodes()
                    .flatMap { it.first.transactions.nodes().map { it.first } }
                    .asFlow()
            }
            .filter {
                // Only transactions to regular non-system addresses
                ((it.in_msg.value?.info as? IntMsgInfo)?.dest as? AddrStd)?.let { it !in SYSTEM_ADDRESSES } ?: false
            }
            .onEach {
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
            .shareIn(CoroutineScope(Dispatchers.Default), SharingStarted.Lazily, 100)

    companion object : KLogging()
}
