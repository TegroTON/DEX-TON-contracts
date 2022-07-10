package money.tegro.dex.contract

import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import net.logstash.logback.argument.StructuredArguments.value
import net.logstash.logback.marker.Markers.append
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import org.ton.block.VmStackValue
import org.ton.cell.Cell
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

interface JettonContract {
    val totalSupply: BigInt
    val mintable: Boolean
    val admin: MsgAddressInt
    val content: Cell
    val walletCode: Cell

    suspend fun metadata(): JettonMetadata
    
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(address: AddrStd, liteApi: LiteApi): JettonContract {
            val referenceBlock = liteApi.getMasterchainInfo().last
            logger.trace("reference block no. {}", v("seqno", referenceBlock.seqno))

            return liteApi.runSmcMethod(0b100, referenceBlock, LiteServerAccountId(address), "get_jetton_data").let {
                logger.debug(append("result", it), "smc method exit code {}", value("exitCode", it.exitCode))
                require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }

                JettonContractImpl(
                    totalSupply = it[0]!!.asBigInt(),
                    mintable = (it[1] as VmStackValue.TinyInt).value != 0L,
                    admin = (it[2] as VmStackValue.Slice).toCellSlice().loadTlb(MsgAddressInt),
                    content = (it[3] as VmStackValue.Cell).cell,
                    walletCode = (it[4] as VmStackValue.Cell).cell,
                )
            }
        }
    }
}
