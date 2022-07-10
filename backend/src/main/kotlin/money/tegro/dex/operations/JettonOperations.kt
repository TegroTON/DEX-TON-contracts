package money.tegro.dex.operations

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import money.tegro.dex.dto.JettonDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller("/jettons")
@Tag(name = "Jettons", description = "Information about jettons")
interface JettonOperations {
    @Operation(summary = "Get information about all available jettons")
    @Get("/")
    fun allJettons(): Flux<JettonDTO>

    @Operation(summary = "Get information about specific jetton")
    @Get("/{symbol}")
    fun getJetton(symbol: String): Mono<JettonDTO>
}
