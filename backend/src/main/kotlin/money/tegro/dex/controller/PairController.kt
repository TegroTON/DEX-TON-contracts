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
import org.ton.block.MsgAddressInt

@Controller
open class PairController(
    private val pairRepository: PairRepository,
    private val tokenRepository: TokenRepository,
) : PairOperations {
    @Timed("controller.pair.all")
    override fun all(): Flow<PairDTO> =
        pairRepository.findByEnabledTrue()
            .map(::mapPair)

    @Timed("controller.pair.find")
    override suspend fun find(left: String, right: String): PairDTO {
        val leftModel =
            requireNotNull(tokenRepository.findBySymbolAndEnabledTrue(left.uppercase())) { "Token `$left` is unknown" }
        val rightModel =
            requireNotNull(tokenRepository.findBySymbolAndEnabledTrue(right.uppercase())) { "Token `$right` is unknown" }

        return requireNotNull(
            // In the DB it could be stored either way for XXX/XXX, find the right combination
            ((rightModel.address as? MsgAddressInt)?.let {
                pairRepository.findByBaseAndQuoteAndEnabledTrue(leftModel.address, it)
            } ?: (leftModel.address as? MsgAddressInt)?.let {
                pairRepository.findByBaseAndQuoteAndEnabledTrue(rightModel.address, it)
            })
                ?.let { mapPair(it, leftModel, rightModel) }
        ) { "Unknown pair `$left -> $right`" }
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
            leftAddress = (leftModel.address as? AddrStd)?.toSafeBounceable(),
            leftReserved = if (model.base == leftModel.address) model.baseReserve else model.quoteReserve,
            rightName = rightModel.name,
            rightSymbol = rightModel.symbol,
            rightAddress = (rightModel.address as? AddrStd)?.toSafeBounceable(),
            rightReserved = if (model.base == rightModel.address) model.baseReserve else model.quoteReserve,
        )
}
