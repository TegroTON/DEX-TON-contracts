package money.tegro.dex.controller

import io.micrometer.core.annotation.Timed
import io.micronaut.http.annotation.Controller
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.dto.PairDTO
import money.tegro.dex.model.PairModel
import money.tegro.dex.model.TokenModel
import money.tegro.dex.operations.PairOperations
import money.tegro.dex.repository.PairRepository
import money.tegro.dex.repository.TokenRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Controller
open class PairController(
    private val pairRepository: PairRepository,
    private val tokenRepository: TokenRepository,
) : PairOperations {
    @Timed("controller.pair.all")
    override fun all(): Flux<PairDTO> =
        pairRepository.findAll()
            .flatMap(::mapPair)

    @Timed("controller.pair.find")
    override fun find(left: String, right: String): Mono<PairDTO> = mono {
        val leftModel =
            tokenRepository.findBySymbol(left.uppercase()).awaitSingle()

        val rightModel =
            tokenRepository.findBySymbol(right.uppercase()).awaitSingle()

        pairRepository.findByBaseAndQuote(leftModel.address, rightModel.address)
            .switchIfEmpty { pairRepository.findByBaseAndQuote(rightModel.address, leftModel.address) }
            .flatMap { mapPair(it, leftModel, rightModel) }
            .awaitSingleOrNull()
    }

    private fun mapPair(model: PairModel) = mono {
        mapPair(
            model,
            tokenRepository.findById(model.base).awaitSingle(),
            tokenRepository.findById(model.quote).awaitSingle()
        ).awaitSingleOrNull()
    }

    private fun mapPair(model: PairModel, leftModel: TokenModel, rightModel: TokenModel) = mono {
        PairDTO(
            updated = model.updated.epochSecond,
            address = model.address.toSafeBounceable(),
            leftName = leftModel.name,
            leftSymbol = leftModel.symbol,
            leftAddress = leftModel.address.toSafeBounceable(),
            leftReserved = if (model.base == leftModel.address) model.baseReserve else model.quoteReserve,
            rightName = rightModel.name,
            rightSymbol = rightModel.symbol,
            rightAddress = rightModel.address.toSafeBounceable(),
            rightReserved = if (model.base == rightModel.address) model.baseReserve else model.quoteReserve,
        )
    }
}
