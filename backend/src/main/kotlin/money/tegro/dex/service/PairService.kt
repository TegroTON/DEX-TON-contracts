package money.tegro.dex.service

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.event.StartupEvent
import io.micronaut.data.model.Sort
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.config.ServiceConfig
import money.tegro.dex.contract.PairContract
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.repository.PairRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import java.time.Duration

@Singleton
open class PairService(
    private val config: ServiceConfig,
    private val registry: MeterRegistry,

    private val liteApi: LiteApi,
    private val liveAccounts: Flux<AddrStd>,

    private val pairRepository: PairRepository,
) {
    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        liveAccounts
            .concatMap { pairRepository.findById(it) }
            .doOnNext {
                registry.counter("service.pair.hits").increment()
                logger.info("{} matched database entity", kv("address", it.address.toSafeBounceable()))
            }
            .mergeWith {
                // Apart from watching live interactions, update them periodically
                Flux.interval(Duration.ZERO, config.pairPeriod)
                    .concatMap { pairRepository.findAll(Sort.of(Sort.Order.asc("updated"))) }
            }
            .concatMap {
                mono { it.address to PairContract.getReserves(it.address, liteApi) }
                    .timed()
                    .doOnNext {
                        registry.timer("service.watch.pair")
                            .record(it.elapsed())
                    }
            }
            .subscribe {
                val (address, reserves) = it.get()
                pairRepository.update(address, reserves.first, reserves.second)
            }
    }

    companion object : KLogging()
}
