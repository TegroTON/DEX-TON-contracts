package money.tegro.dex.controller

import io.micrometer.core.annotation.Timed
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.server.types.files.StreamedFile
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.dto.TokenDTO
import money.tegro.dex.model.TokenModel
import money.tegro.dex.operations.TokenOperations
import money.tegro.dex.repository.TokenRepository
import org.ton.crypto.base64
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayInputStream
import java.net.URI
import java.net.URL

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

    @Timed("controller.token.icon")
    override fun image(symbol: String): Mono<HttpResponse<StreamedFile>> =
        tokenRepository.findBySymbol(symbol)
            .map {
                if (it.image.startsWith("http")) { // probably IPFS or something, cache that
                    HttpResponse.ok(StreamedFile(URL(it.image)))
                } else if (it.image.startsWith("data:")) { // data url, extract binary data
                    HttpResponse.ok(
                        StreamedFile(
                            ByteArrayInputStream(
                                it.image.substring(it.image.lastIndexOf(',') + 1).let { base64(it) }
                            ),
                            MediaType.ALL_TYPE
                        )
                    )
                } else { // Data url or something, just redirect
                    HttpResponse.redirect(URI(it.image))
                }
            }

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
