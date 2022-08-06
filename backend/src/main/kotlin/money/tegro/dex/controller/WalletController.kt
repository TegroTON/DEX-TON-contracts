package money.tegro.dex.controller

import io.micrometer.core.annotation.Timed
import io.micronaut.http.annotation.Controller
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.dto.WalletDTO
import money.tegro.dex.model.TokenModel
import money.tegro.dex.model.WalletModel
import money.tegro.dex.operations.WalletOperations
import money.tegro.dex.repository.TokenRepository
import money.tegro.dex.service.WalletService
import org.ton.block.AddrStd

@Controller
open class WalletController(
    private val tokenRepository: TokenRepository,
    private val walletService: WalletService,
) : WalletOperations {
    @Timed("controller.wallet.all")
    override fun getAll(address: String): Flow<WalletDTO> =
        tokenRepository.findAll()
            .map { token ->
                mapWallet(walletService.getWallet(AddrStd(address), token.address), token)
            }

    @Timed("controller.wallet.get")
    override suspend fun get(address: String, symbol: String): WalletDTO {
        val token = requireNotNull(tokenRepository.findBySymbolAndEnabledTrue(symbol))
        return mapWallet(walletService.getWallet(AddrStd(address), token.address), token)
    }

    private fun mapWallet(wallet: WalletModel, token: TokenModel) = WalletDTO(
        updated = wallet.updated.epochSecond,
        address = (wallet.address as AddrStd).toSafeBounceable(),
        balance = wallet.balance,
        owner = (wallet.owner as AddrStd).toSafeBounceable(),
        master = (wallet.master as? AddrStd)?.toSafeBounceable(),
        supply = token.supply,
        name = token.name,
        description = token.description,
        symbol = token.symbol,
        decimals = token.decimals,
    )
}
