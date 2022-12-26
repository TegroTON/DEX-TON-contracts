package money.tegro.dex.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    name = "Ticker",
    description = "24-hour pricing and volume summary for each market pair available on the exchange"
)
data class TickerDTO(
    @field:Schema(description = "Last transacted price of base currency based on given quote currency")
    val lastPrice: Double,

    @field:Schema(description = "24-hour trading volume denoted in BASE currency")
    val baseVolume: Double,

    @field:Schema(description = "24 hour trading volume denoted in QUOTE currency")
    val quoteVolume: Double,
)
