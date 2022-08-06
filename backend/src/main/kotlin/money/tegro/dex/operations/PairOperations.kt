package money.tegro.dex.operations

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import money.tegro.dex.dto.PairDTO

@Controller("/pairs")
@Tag(name = "Pairs", description = "Information about exchange pairs")
interface PairOperations {
    @Operation(summary = "Get information about all pairs")
    @Get("/")
    fun all(): Flow<PairDTO>

    @Operation(summary = "Get information about specific exchange pair")
    @Get("/{left}/{right}")
    suspend fun find(left: String, right: String): PairDTO
}
