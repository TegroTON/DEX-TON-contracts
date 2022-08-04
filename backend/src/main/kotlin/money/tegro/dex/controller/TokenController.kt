package money.tegro.dex.controller

import io.micrometer.core.annotation.Timed
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.server.types.files.StreamedFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.dto.TokenDTO
import money.tegro.dex.model.TokenModel
import money.tegro.dex.operations.TokenOperations
import money.tegro.dex.repository.TokenRepository
import org.ton.block.AddrStd
import org.ton.crypto.base64
import java.io.ByteArrayInputStream
import java.net.URI
import java.net.URL

@Controller
open class TokenController(
    private val tokenRepository: TokenRepository,
) : TokenOperations {
    @Timed("controller.token.all")
    override suspend fun all(): Flow<TokenDTO> =
        tokenRepository.findAll()
            .map(::mapToken)

    @Timed("controller.token.find")
    override suspend fun find(symbol: String): TokenDTO =
        requireNotNull(
            tokenRepository.findBySymbol(symbol)
                ?.let(::mapToken)
        ) { "Unknown token `$symbol`" }

    @Timed("controller.token.icon")
    override suspend fun image(symbol: String): HttpResponse<StreamedFile> =
        requireNotNull(tokenRepository.findBySymbol(symbol)) { "Unknown token `$symbol`" }
            .let {
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

    private fun mapToken(model: TokenModel) =
        TokenDTO(
            updated = model.updated.epochSecond,
            address = (model.address as AddrStd).toSafeBounceable(),
            supply = model.supply,
            name = model.name,
            description = model.description,
            symbol = model.symbol,
            decimals = model.decimals,
        )
}
