package money.tegro.dex.operations

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import money.tegro.dex.dto.ExchangePairDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller("/pairs")
@Tag(name = "Pairs", description = "Information about exchange pairs")
interface ExchangePairOperations {
    @Operation(summary = "Get information about all exchange pairs")
    @Get("/")
    fun allPairs(): Flux<ExchangePairDTO>

    @Operation(summary = "Get information about TON/Jetton exchange pairs")
    @Get("/ton")
    fun allToncoinPairs(): Flux<ExchangePairDTO>

    @Operation(summary = "Get information about Jetton/Jetton exchange pairs")
    @Get("/jetton")
    fun allJettonPairs(): Flux<ExchangePairDTO>

    @Operation(summary = "Get information about a specific exchange pair")
    @Get("/{left}/{right}")
    fun getPair(left: String, right: String): Mono<ExchangePairDTO>
}
