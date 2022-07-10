package money.tegro.dex.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.ton.bigint.BigInt

@Schema(name = "Pair", description = "Information about an exchange pair")
data class ExchangePairDTO(
    @get:Schema(description = "Unix timestamp of the last time data was updated")
    val updated: Long,

    @get:Schema(description = "Address of the exchange pair, base64, bounceable")
    val address: String,

    @get:Schema(description = "Full name of the first currency")
    val leftName: String,

    @get:Schema(description = "Symbol, ticker, of the first currency")
    val leftSymbol: String,

    @get:Schema(description = "Address of the left currency, null in case of TON; base64url, bounceable")
    val leftAddress: String?,

    @get:Schema(description = "Volume of the left currency")
    val leftVolume: BigInt,

    @get:Schema(description = "Full name of the second currency")
    val rightName: String,

    @get:Schema(description = "Symbol, ticker, of the second currency")
    val rightSymbol: String,

    @get:Schema(description = "Address of the right currency, base64url, bounceable")
    val rightAddress: String,

    @get:Schema(description = "Volume of the right currency")
    val rightVolume: BigInt,

    @get:Schema(description = "Price denominated in right/left")
    val price: BigInt,

    @get:Schema(description = "Liquidity in (nano)TON")
    val liquidity: BigInt,
)
