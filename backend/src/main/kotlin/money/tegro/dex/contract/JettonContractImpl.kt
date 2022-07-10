package money.tegro.dex.contract

import io.ktor.client.*
import org.ton.bigint.BigInt
import org.ton.block.MsgAddressInt
import org.ton.cell.Cell

data class JettonContractImpl(
    override val totalSupply: BigInt,
    override val mintable: Boolean,
    override val admin: MsgAddressInt,
    override val content: Cell,
    override val walletCode: Cell
) : JettonContract {
    override suspend fun metadata(): JettonMetadata = JettonMetadata.of(content, HttpClient { })
}
