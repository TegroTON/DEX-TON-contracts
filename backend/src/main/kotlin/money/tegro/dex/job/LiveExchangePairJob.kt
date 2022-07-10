package money.tegro.dex.job

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.ExchangePairContract
import money.tegro.dex.repository.ExchangePairRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.lite.api.LiteApi
import reactor.kotlin.core.publisher.toFlux

@Singleton
class LiveExchangePairJob(
    private val liteApi: LiteApi,
    private val exchangePairRepository: ExchangePairRepository,
) : AbstractLiveJob(liteApi) {
    @Scheduled(initialDelay = "0s") // Set it up as soon as possible
    fun run() {
        liveBlocks()
            .flatMap { extractAffectedAccounts(it).toFlux() }
            .filter { it !in SYSTEM_ADDRESSES }
            .doOnNext { logger.debug("affected account {}", v("address", it)) }
            .subscribe {
                exchangePairRepository.findById(it)
                    .subscribe {
                        mono {
                            logger.info("address {} matched database exchange pair entity", v("address", it.address))

                            val reserves = ExchangePairContract.getReserves(it.address, liteApi)

                            exchangePairRepository.update(it.address, reserves.first, reserves.second)
                        }.subscribe()
                    }
            }
    }

    companion object : KLogging()
}
