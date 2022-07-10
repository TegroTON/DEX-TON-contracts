package money.tegro.dex.job

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
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

abstract class AbstractLiveJob(
    private val liteApi: LiteApi,
) {
    fun liveBlocks() = Flux.interval(Duration.ofSeconds(2))
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

    fun extractAffectedAccounts(block: Block) =
        block.extra.account_blocks.nodes()
            .flatMap {
                sequenceOf(AddrStd(block.info.shard.workchain_id, it.first.account_addr))
                    .plus(it.first.transactions.nodes().map {
                        AddrStd(block.info.shard.workchain_id, it.first.account_addr)
                    })
                    .distinct()
            }

    companion object : KLogging() {
        val SYSTEM_ADDRESSES = listOf(
            AddrStd(-1, BitString.of("5555555555555555555555555555555555555555555555555555555555555555")),
            AddrStd(-1, BitString.of("3333333333333333333333333333333333333333333333333333333333333333")),
            AddrStd(-1, BitString.of("0000000000000000000000000000000000000000000000000000000000000000")),
        )

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
