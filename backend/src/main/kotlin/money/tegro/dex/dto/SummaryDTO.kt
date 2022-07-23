package money.tegro.dex.dto

import io.swagger.v3.oas.annotations.media.Schema


@Schema(name = "Summary", description = "Overview of market data for all tickers and all pairs")
data class SummaryDTO(
    @field:Schema(
        description = "Identifier of a ticker with delimiter to separate base/quote, eg. BTC-USD (Price of BTC is quoted in USD)"
    )
    val tradingPairs: String,

    @field:Schema(description = "Symbol/currency code of base currency, eg. BTC")
    val baseCurrency: String,

    @field:Schema(description = "Symbol/currency code of quote currency, eg. USD")
    val quoteCurrency: String,

    @field:Schema(description = "Last transacted price of base currency based on given quote currency")
    val lastPrice: Double,

    @field:Schema(description = "Lowest Ask price of base currency based on given quote currency")
    val lowestAsk: Double,

    @field:Schema(description = "Highest bid price of base currency based on given quote currency")
    val highestBid: Double,

    @field:Schema(description = "24-hr volume of market pair denoted in BASE currency")
    val baseVolume: Double,

    @field:Schema(description = "24-hr volume of market pair denoted in QUOTE currency")
    val quoteVolume: Double,

    @field:Schema(description = "24-hr % price change of market pair")
    val priceChangePercent24h: Double,

    @field:Schema(description = "Highest price of base currency based on given quote currency in the last 24-hrs")
    val highestPrice24h: Double,

    @field:Schema(description = "Lowest price of base currency based on given quote currency in the last 24-hrs")
    val lowestPrice24h: Double,
)
