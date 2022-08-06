package money.tegro.dex.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import org.ton.bigint.BigInt

@Schema(name = "Wallet", description = "Information about wallet holding token balance")
data class WalletDTO(
    @field:Schema(description = "Unix timestamp of the last time data was updated")
    val updated: Long,

    @field:Schema(description = "Contract address, uniquely identifies this specific wallet; Always base64url, bounceable")
    val address: String,

    @field:Schema(description = "Balance", type = "integer")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    val balance: BigInt,

    @field:Schema(description = "Owner of this wallet, for TON this is equal to `address`; Always base64url, bounceable")
    val owner: String,

    @field:Schema(description = "Token master contract, `null` for TON; Always base64url, bounceable")
    val master: String?,

    @field:Schema(description = "Total token supply", type = "integer")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    val supply: BigInt,

    @field:Schema(description = "Full token name")
    val name: String,

    @field:Schema(description = "Description of the token")
    val description: String,

    @field:Schema(description = "Ticker of this token, uniquely identifies this token")
    val symbol: String,

    @field:Schema(description = "Number of decimals in token's representation")
    val decimals: Int,
)