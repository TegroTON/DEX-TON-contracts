package money.tegro.dex.contract

import mu.KLogging
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import org.ton.block.VmStackValue
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
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
        suspend fun of(address: AddrStd, liteClient: LiteClient): TokenContract =
            liteClient.runSmcMethod(LiteServerAccountId(address), "get_jetton_data").toMutableVmStack().let {
                TokenContract(
                    totalSupply = it.popNumber().toBigInt(),
                    mintable = it.popNumber().toLong() != 0L,
                    admin = it.popSlice().loadTlb(MsgAddressInt),
                    content = it.popCell(),
                    walletCode = it.popCell(),
                )
            }

        @JvmStatic
        suspend fun getWalletAddress(address: AddrStd, owner: MsgAddressInt, liteClient: LiteClient): MsgAddressInt =
            liteClient.runSmcMethod(
                LiteServerAccountId(address),
                "get_wallet_address",
                VmStackValue.of(CellBuilder.createCell { storeTlb(MsgAddressInt, owner) }.beginParse())
            ).toMutableVmStack().popSlice().loadTlb(MsgAddressInt)
    }
}
