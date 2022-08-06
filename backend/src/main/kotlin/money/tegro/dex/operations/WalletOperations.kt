package money.tegro.dex.operations

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import money.tegro.dex.dto.WalletDTO

@Controller("/wallets")
@Tag(name = "Wallets", description = "Information about wallets: jettons,liquidity-pool tokens, native TON")
interface WalletOperations {
    @Operation(summary = "Get information about all balances associated with the address")
    @Get("/{address}")
    suspend fun getAll(address: String): Flow<WalletDTO>

    @Operation(summary = "Get information about balance of specific token associated with the address")
    @Get("/{address}/{symbol}")
    suspend fun get(address: String, symbol: String): WalletDTO
}
