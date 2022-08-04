package money.tegro.dex.controller

import io.micrometer.core.annotation.Timed
import io.micronaut.http.annotation.Controller
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.dto.PairDTO
import money.tegro.dex.model.PairModel
import money.tegro.dex.model.TokenModel
import money.tegro.dex.operations.PairOperations
import money.tegro.dex.repository.PairRepository
import money.tegro.dex.repository.TokenRepository
import org.ton.block.AddrStd

@Controller
open class PairController(
    private val pairRepository: PairRepository,
    private val tokenRepository: TokenRepository,
) : PairOperations {
    @Timed("controller.pair.all")
    override suspend fun all(): Flow<PairDTO> =
        pairRepository.findAll()
            .map(::mapPair)

    @Timed("controller.pair.find")
    override suspend fun find(left: String, right: String): PairDTO {
        val leftModel = requireNotNull(tokenRepository.findBySymbol(left.uppercase())) { "Token `$left` is unknown" }
        val rightModel = requireNotNull(tokenRepository.findBySymbol(right.uppercase())) { "Token `$right` is unknown" }

        return requireNotNull(
            pairRepository.findByBaseAndQuote(leftModel.address, rightModel.address)
                ?.let { mapPair(it, leftModel, rightModel) }
                ?: pairRepository.findByBaseAndQuote(rightModel.address, leftModel.address)
                    ?.let { mapPair(it, rightModel, leftModel) })
        { "Unknown pair `$left -> $right`" }
    }

    private suspend fun mapPair(model: PairModel) =
        mapPair(
            model,
            requireNotNull(tokenRepository.findById(model.base)),
            requireNotNull(tokenRepository.findById(model.quote))
        )

    private fun mapPair(model: PairModel, leftModel: TokenModel, rightModel: TokenModel) =
        PairDTO(
            updated = model.updated.epochSecond,
            address = (model.address as AddrStd).toSafeBounceable(),
            leftName = leftModel.name,
            leftSymbol = leftModel.symbol,
            leftAddress = (leftModel.address as AddrStd).toSafeBounceable(),
            leftReserved = if (model.base == leftModel.address) model.baseReserve else model.quoteReserve,
            rightName = rightModel.name,
            rightSymbol = rightModel.symbol,
            rightAddress = (rightModel.address as AddrStd).toSafeBounceable(),
            rightReserved = if (model.base == rightModel.address) model.baseReserve else model.quoteReserve,
        )
}
