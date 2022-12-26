package money.tegro.dex.contract

import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import net.logstash.logback.argument.StructuredArguments.value
import net.logstash.logback.marker.Markers.append
import org.ton.bigint.BigInt
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.api.liteserver.functions.LiteServerGetMasterchainInfo
import org.ton.lite.api.liteserver.functions.LiteServerRunSmcMethod
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

data class TokenContract(
    val totalSupply: BigInt,
    val mintable: Boolean,
    val admin: MsgAddressInt,
    val content: Cell,
    val walletCode: Cell
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(address: AddrStd, liteApi: LiteApi): TokenContract {
            val referenceBlock = liteApi(LiteServerGetMasterchainInfo).last
            logger.trace("reference block no. {}", v("seqno", referenceBlock.seqno))

            return liteApi(
                LiteServerRunSmcMethod(
                    0b100,
                    referenceBlock,
                    LiteServerAccountId(address),
                    "get_jetton_data"
                )
            ).let {
                logger.debug(append("result", it), "smc method exit code {}", value("exitCode", it.exitCode))
                require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }

                val stack = it.parseAsVmStack()!!.value
                TokenContract(
                    totalSupply = stack[0].asBigInt(),
                    mintable = (stack[1] as VmStackNumber).toLong() != 0L,
                    admin = (stack[2] as VmStackSlice).toCellSlice().loadTlb(MsgAddressInt),
                    content = (stack[3] as VmStackCell).cell,
                    walletCode = (stack[4] as VmStackCell).cell,
                )
            }
        }

        @JvmStatic
        suspend fun getWalletAddress(address: AddrStd, owner: MsgAddressInt, liteApi: LiteApi): MsgAddressInt {
            val referenceBlock = liteApi(LiteServerGetMasterchainInfo).last
            logger.trace("reference block no. {}", v("seqno", referenceBlock.seqno))

            return liteApi(
                LiteServerRunSmcMethod(
                    0b100, referenceBlock, LiteServerAccountId(address), "get_wallet_address",
                    VmStack(VmStackList(VmStackValue.of(CellBuilder.createCell { storeTlb(MsgAddressInt, owner) }
                        .beginParse())))
                )
            ).let {
                logger.debug(append("result", it), "smc method exit code {}", value("exitCode", it.exitCode))
                require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }
                val stack = it.parseAsVmStack()!!.value
                (stack[0] as VmStackSlice).toCellSlice().loadTlb(MsgAddressInt)
            }
        }
    }
}
