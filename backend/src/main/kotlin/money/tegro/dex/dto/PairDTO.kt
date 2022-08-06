package money.tegro.dex.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import org.ton.bigint.BigInt

@Schema(name = "Pair", description = "Information about an exchange pair")
data class PairDTO(
    @field:Schema(description = "Unix timestamp of the last time data was updated")
    val updated: Long,

    @field:Schema(description = "Address of the exchange pair, base64, bounceable")
    val address: String,

    @field:Schema(description = "Full name of the first token")
    val leftName: String,

    @field:Schema(description = "Symbol, ticker, of the first token. Uniquely identifies it")
    val leftSymbol: String,

    @field:Schema(description = "Address of the left token, `null` for TON; base64url, bounceable")
    val leftAddress: String?,

    @field:Schema(description = "Reserved amount in the liquidity pool for the left token", type = "integer")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    val leftReserved: BigInt,

    @field:Schema(description = "Full name of the second token")
    val rightName: String,

    @field:Schema(description = "Symbol, ticker, of the second token. Uniquely identifies it")
    val rightSymbol: String,

    @field:Schema(description = "Address of the right token, `null` for TON; base64url, bounceable")
    val rightAddress: String?,

    @field:Schema(description = "Reserved amount in the liquidity pool for the right token", type = "integer")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    val rightReserved: BigInt,
)
