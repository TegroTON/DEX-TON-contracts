package money.tegro.dex.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.ton.bigint.BigInt

@Schema(name = "Currency", description = "Information about a specific currency (toncoin or any jetton)")
data class CurrencyDTO(
    @get:Schema(description = "Unix timestamp of the last time data was updated")
    val updated: Long,

    @get:Schema(description = "Full name of the currency")
    val name: String,

    @get:Schema(description = "Symbol, ticker of the currency")
    val symbol: String,

    @get:Schema(description = "Jetton contract address, null for TON; Always base64url, bounceable")
    val address: String,

    @get:Schema(description = "Price in (nano)TON")
    val price: BigInt,
)
