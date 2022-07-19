package money.tegro.dex.controller

import io.micrometer.core.annotation.Timed
import io.micronaut.http.annotation.Controller
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.dto.ExchangePairDTO
import money.tegro.dex.model.ExchangePairModel
import money.tegro.dex.model.JettonModel
import money.tegro.dex.operations.ExchangePairOperations
import money.tegro.dex.repository.ExchangePairRepository
import money.tegro.dex.repository.JettonRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
open class ExchangePairController(
    private val exchangePairRepository: ExchangePairRepository,
    private val jettonRepository: JettonRepository,
) : ExchangePairOperations {
    @Timed
    override fun allPairs(): Flux<ExchangePairDTO> =
        exchangePairRepository.findAll()
            .flatMap(::mapPair)

    @Timed
    override fun allToncoinPairs(): Flux<ExchangePairDTO> =
        exchangePairRepository.findByLeftIsNull()
            .flatMap(::mapPair)

    @Timed
    override fun allJettonPairs(): Flux<ExchangePairDTO> =
        exchangePairRepository.findByLeftIsNotNull()
            .flatMap(::mapPair)

    override fun getPair(left: String, right: String): Mono<ExchangePairDTO> = mono {
        val leftModel =
            if (left.equals("TON", ignoreCase = true)) null else jettonRepository.findBySymbol(left).awaitSingle()
        val rightModel = jettonRepository.findBySymbol(right).awaitSingle()

        exchangePairRepository.findByLeftAndRight(leftModel?.address, rightModel.address)
            .flatMap { mapPair(it, leftModel, rightModel) }
            .awaitSingleOrNull()
    }

    private fun mapPair(model: ExchangePairModel) = mono {
        val leftModel = model.left?.let { jettonRepository.findById(it).awaitSingle() }
        val rightModel = jettonRepository.findById(model.right).awaitSingle()

        mapPair(model, leftModel, rightModel).awaitSingleOrNull()
    }

    private fun mapPair(model: ExchangePairModel, leftModel: JettonModel?, rightModel: JettonModel) = mono {
        require(model.left == leftModel?.address) // Sanity checks just in case
        require(model.right == rightModel.address)

        ExchangePairDTO(
            updated = model.updated.epochSecond,
            address = model.address.toSafeBounceable(),
            leftName = leftModel?.let { it.name ?: "Unknown Jetton" } ?: "Toncoin",
            leftSymbol = leftModel?.let { it.symbol ?: "UNKNOWN" } ?: "TON",
            leftAddress = model.left?.toSafeBounceable(),
            leftReserved = model.leftReserved,
            rightName = rightModel.name ?: "Unknown Jetton",
            rightSymbol = rightModel.symbol ?: "UNKNOWN",
            rightAddress = model.right.toSafeBounceable(),
            rightReserved = model.rightReserved,
        )
    }
}
