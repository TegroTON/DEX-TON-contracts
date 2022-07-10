package money.tegro.dex.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Jetton", description = "Information about a specific jetton")
data class JettonDTO(
    @field:Schema(description = "Unix timestamp of the last time data was updated")
    val updated: Long,

    @field:Schema(description = "Full name of the jetton")
    val name: String,

    @field:Schema(description = "Symbol, ticker of the jetton")
    val symbol: String,

    @field:Schema(description = "Jetton contract address; Always base64url, bounceable")
    val address: String,
)
