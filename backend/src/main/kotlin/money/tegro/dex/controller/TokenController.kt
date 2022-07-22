package money.tegro.dex.controller

import io.micrometer.core.annotation.Timed
import io.micronaut.http.annotation.Controller
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.dto.TokenDTO
import money.tegro.dex.model.TokenModel
import money.tegro.dex.operations.TokenOperations
import money.tegro.dex.repository.TokenRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
open class TokenController(
    private val tokenRepository: TokenRepository,
) : TokenOperations {
    @Timed("controller.token.all")
    override fun all(): Flux<TokenDTO> =
        tokenRepository.findAll()
            .flatMap(::mapToken)

    @Timed("controller.token.find")
    override fun find(symbol: String): Mono<TokenDTO> =
        tokenRepository.findBySymbol(symbol)
            .flatMap(::mapToken)

    private fun mapToken(model: TokenModel) = mono {
        TokenDTO(
            updated = model.updated.epochSecond,
            address = model.address.toSafeBounceable(),
            supply = model.supply,
            name = model.name,
            description = model.description,
            symbol = model.symbol,
            decimals = model.decimals,
        )
    }
}
