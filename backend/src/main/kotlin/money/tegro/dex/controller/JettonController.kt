package money.tegro.dex.controller

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.http.annotation.Controller
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.dto.JettonDTO
import money.tegro.dex.model.JettonModel
import money.tegro.dex.operations.JettonOperations
import money.tegro.dex.repository.JettonRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
open class JettonController(
    private val meterRegistry: MeterRegistry,

    private val jettonRepository: JettonRepository,
) : JettonOperations {
    @Timed
    override fun allJettons(): Flux<JettonDTO> =
        jettonRepository.findAll()
            .flatMap(::mapJetton)

    @Timed
    override fun getJetton(symbol: String): Mono<JettonDTO> =
        jettonRepository.findBySymbol(symbol)
            .flatMap(::mapJetton)

    private fun mapJetton(model: JettonModel) = mono {
        JettonDTO(
            updated = model.updated.epochSecond,
            name = model.name ?: "Unknown Jetton",
            symbol = model.symbol ?: "UNKNOWN",
            address = model.address.toSafeBounceable()
        )
    }
}
