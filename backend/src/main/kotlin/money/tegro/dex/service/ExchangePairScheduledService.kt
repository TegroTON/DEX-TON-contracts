package money.tegro.dex.service

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.ExchangePairContract
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.repository.ExchangePairRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.lite.api.LiteApi

@Singleton
class ExchangePairScheduledService(
    private val liteApi: LiteApi,
    private val exchangePairRepository: ExchangePairRepository,
) {
    @Scheduled(initialDelay = "0s", fixedDelay = "10m")
    fun run() {
        logger.info { "updating exchange pair information" }

        exchangePairRepository.findAll()
            .doOnNext {
                logger.info(
                    "updating {} exchange pair reserves",
                    v("address", it.address.toSafeBounceable())
                )
            }
            .flatMap { mono { it.address to ExchangePairContract.getReserves(it.address, liteApi) } }
            .doOnNext {
                val (address, reserves) = it
                exchangePairRepository.update(address, reserves.first, reserves.second)
            }
            .blockLast()

        logger.info { "all done" }
    }

    companion object : KLogging()
}
