package money.tegro.dex.operations

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.server.types.files.StreamedFile
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import money.tegro.dex.dto.TokenDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller("/tokens")
@Tag(name = "Tokens", description = "Information about tokens: jettons and liquidity-pool tokens")
interface TokenOperations {
    @Operation(summary = "Get information about all available tokens, includes LP-tokens")
    @Get("/")
    fun all(): Flux<TokenDTO>

    @Operation(summary = "Get information about specific token")
    @Get("/{symbol}")
    fun find(symbol: String): Mono<TokenDTO>

    @Operation(summary = "Get token's icon")
    @Get("/{symbol}/image")
    fun image(symbol: String): Mono<HttpResponse<StreamedFile>>
}
