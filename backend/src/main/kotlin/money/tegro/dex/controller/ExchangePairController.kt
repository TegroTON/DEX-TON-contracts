package money.tegro.dex.controller

import money.tegro.dex.dto.ExchangePairDTO
import money.tegro.dex.operations.ExchangePairOperations
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ExchangePairController : ExchangePairOperations {
    override fun allPairs(): Flux<ExchangePairDTO> {
        TODO("Not yet implemented")
    }

    override fun allToncoinPairs(): Flux<ExchangePairDTO> {
        TODO("Not yet implemented")
    }

    override fun allJettonPairs(): Flux<ExchangePairDTO> {
        TODO("Not yet implemented")
    }

    override fun getPair(left: String, right: String): Mono<ExchangePairDTO> {
        TODO("Not yet implemented")
    }

}
