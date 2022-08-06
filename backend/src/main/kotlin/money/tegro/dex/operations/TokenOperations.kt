package money.tegro.dex.operations

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.server.types.files.StreamedFile
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import money.tegro.dex.dto.TokenDTO

@Controller("/tokens")
@Tag(name = "Tokens", description = "Information about tokens: jettons and liquidity-pool tokens")
interface TokenOperations {
    @Operation(summary = "Get information about all available tokens, includes LP-tokens")
    @Get("/")
    fun all(): Flow<TokenDTO>

    @Operation(summary = "Get information about specific token")
    @Get("/{symbol}")
    suspend fun find(symbol: String): TokenDTO

    @Operation(summary = "Get token's icon")
    @Get("/{symbol}/image")
    suspend fun image(symbol: String): HttpResponse<StreamedFile>
}
