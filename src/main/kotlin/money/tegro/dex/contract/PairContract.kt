package money.tegro.dex.contract

import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.value
import net.logstash.logback.marker.Markers.append
import org.ton.bigint.BigInt
import org.ton.block.*
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.api.liteserver.functions.LiteServerGetMasterchainInfo
import org.ton.lite.api.liteserver.functions.LiteServerRunSmcMethod

interface PairContract {
    companion object : KLogging() {
        @JvmStatic
        suspend fun isInitialized(address: AddrStd, liteApi: LiteApi): Boolean {
            val referenceBlock = liteApi(LiteServerGetMasterchainInfo).last
            logger.trace("reference block no. {}", value("seqno", referenceBlock.seqno))

            return liteApi(
                LiteServerRunSmcMethod(
                    0b100,
                    referenceBlock,
                    LiteServerAccountId(address),
                    "initialized"
                )
            ).let {
                logger.debug(append("result", it), "smc method exit code {}", value("exitCode", it.exitCode))
                require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }

                (it.parseAsVmStack()!!.value[0] as VmStackNumber).toLong() != 0L
            }
        }

        @JvmStatic
        suspend fun getReserves(address: AddrStd, liteApi: LiteApi): Pair<BigInt, BigInt> {
            val referenceBlock = liteApi(LiteServerGetMasterchainInfo).last
            logger.trace("reference block no. {}", value("seqno", referenceBlock.seqno))

            return liteApi(
                LiteServerRunSmcMethod(
                    0b100,
                    referenceBlock,
                    LiteServerAccountId(address),
                    "get_reserves"
                )
            ).let {
                logger.debug(append("result", it), "smc method exit code {}", value("exitCode", it.exitCode))
                require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }

                val stack = it.parseAsVmStack()!!.value
                stack[0].asBigInt() to stack[1].asBigInt()
            }
        }

        @JvmStatic
        suspend fun getLpShare(address: AddrStd, amount: BigInt, liteApi: LiteApi): Pair<BigInt, BigInt> {
            val referenceBlock = liteApi(LiteServerGetMasterchainInfo).last
            logger.trace("reference block no. {}", value("seqno", referenceBlock.seqno))

            return liteApi(
                LiteServerRunSmcMethod(
                    0b100,
                    referenceBlock,
                    LiteServerAccountId(address),
                    "get_lp_share",
                    VmStack(VmStackList(VmStackValue.of(amount)))
                )
            ).let {
                logger.debug(append("result", it), "smc method exit code {}", value("exitCode", it.exitCode))
                require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }

                val stack = it.parseAsVmStack()!!.value
                stack[0].asBigInt() to stack[1].asBigInt()
            }
        }
    }
}
