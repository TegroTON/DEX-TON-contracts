package money.tegro.dex.controller

import money.tegro.dex.dto.CurrencyDTO
import money.tegro.dex.operations.JettonOperations
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class JettonController : JettonOperations {
    override fun allJettons(): Flux<CurrencyDTO> {
        TODO("Not yet implemented")
    }

    override fun getJetton(symbol: String): Mono<CurrencyDTO> {
        TODO("Not yet implemented")
    }
}
