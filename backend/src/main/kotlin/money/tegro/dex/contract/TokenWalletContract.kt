package money.tegro.dex.contract

import mu.KLogging
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.cell.Cell
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

data class TokenWalletContract(
    val balance: BigInt,
    val owner: MsgAddress,
    val jetton: MsgAddress,
    val walletCode: Cell
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(address: AddrStd, liteClient: LiteClient): TokenWalletContract =
            liteClient.runSmcMethod(LiteServerAccountId(address), "get_wallet_data").toMutableVmStack().let {
                TokenWalletContract(
                    balance = it.popNumber().toBigInt(),
                    owner = it.popSlice().loadTlb(MsgAddress),
                    jetton = it.popSlice().loadTlb(MsgAddress),
                    walletCode = it.popCell(),
                )
            }
    }
}
