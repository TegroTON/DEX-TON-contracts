package money.tegro.dex.contract

import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.value
import net.logstash.logback.marker.Markers.append
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.VmStackNumber
import org.ton.block.VmStackTinyInt
import org.ton.block.VmStackValue
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId

interface PairContract {
    companion object : KLogging() {
        @JvmStatic
        suspend fun isInitialized(address: AddrStd, liteApi: LiteApi): Boolean {
            val referenceBlock = liteApi.getMasterchainInfo().last
            logger.trace("reference block no. {}", value("seqno", referenceBlock.seqno))

            return liteApi.runSmcMethod(0b100, referenceBlock, LiteServerAccountId(address), "initialized").let {
                logger.debug(append("result", it), "smc method exit code {}", value("exitCode", it.exitCode))
                require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }

                (it[0] as VmStackTinyInt).value != 0L
            }
        }

        @JvmStatic
        suspend fun getReserves(address: AddrStd, liteApi: LiteApi): Pair<BigInt, BigInt> {
            val referenceBlock = liteApi.getMasterchainInfo().last
            logger.trace("reference block no. {}", value("seqno", referenceBlock.seqno))

            return liteApi.runSmcMethod(0b100, referenceBlock, LiteServerAccountId(address), "get_reserves").let {
                logger.debug(append("result", it), "smc method exit code {}", value("exitCode", it.exitCode))
                require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }


                (it[0] as VmStackNumber).toBigInt() to (it[1] as VmStackNumber).toBigInt()
            }
        }

        @JvmStatic
        suspend fun getLpShare(address: AddrStd, amount: BigInt, liteApi: LiteApi): Pair<BigInt, BigInt> {
            val referenceBlock = liteApi.getMasterchainInfo().last
            logger.trace("reference block no. {}", value("seqno", referenceBlock.seqno))

            return liteApi.runSmcMethod(
                0b100,
                referenceBlock,
                LiteServerAccountId(address),
                "get_lp_share",
                VmStackValue.of(amount)
            ).let {
                logger.debug(append("result", it), "smc method exit code {}", value("exitCode", it.exitCode))
                require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }

                (it[0] as VmStackNumber).toBigInt() to (it[1] as VmStackNumber).toBigInt()
            }
        }
    }
}
