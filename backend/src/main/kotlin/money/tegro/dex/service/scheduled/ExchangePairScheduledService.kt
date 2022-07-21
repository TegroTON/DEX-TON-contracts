package money.tegro.dex.service.scheduled

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.config.ScheduledServiceConfig
import money.tegro.dex.contract.ExchangePairContract
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.repository.ExchangePairRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import java.time.Duration

@Singleton
class ExchangePairScheduledService(
    private val registry: MeterRegistry,
    private val config: ScheduledServiceConfig,
    private val liteApi: LiteApi,
    private val exchangePairRepository: ExchangePairRepository,
) {
    @PostConstruct
    private fun setup() {
        Flux.interval(Duration.ZERO, config.exchangePairUpdatePeriod)
            .concatMap { exchangePairRepository.findAll() }
            .doOnNext {
                logger.info(
                    "updating {} exchange pair reserves",
                    v("address", it.address.toSafeBounceable())
                )
            }
            .concatMap {
                mono { it.address to ExchangePairContract.getReserves(it.address, liteApi) }
                    .timed()
                    .doOnNext {
                        registry.timer("service.scheduled.exchangepair")
                            .record(it.elapsed())
                    }
            }
            .subscribe {
                val (address, reserves) = it.get()
                exchangePairRepository.update(address, reserves.first, reserves.second)
            }
    }

    @Scheduled(initialDelay = "0s")
    internal fun run() {
    }

    companion object : KLogging()
}
