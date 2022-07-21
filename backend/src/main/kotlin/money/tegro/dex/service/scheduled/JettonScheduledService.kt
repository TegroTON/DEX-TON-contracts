package money.tegro.dex.service.scheduled

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.config.ScheduledServiceConfig
import money.tegro.dex.contract.JettonContract
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.repository.JettonRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant

@Singleton
class JettonScheduledService(
    private val registry: MeterRegistry,
    private val config: ScheduledServiceConfig,
    private val liteApi: LiteApi,
    private val jettonRepository: JettonRepository,
) {
    @PostConstruct
    private fun setup() {
        Flux.interval(Duration.ZERO, config.jettonUpdatePeriod)
            .concatMap { jettonRepository.findAll() }
            .doOnNext { logger.debug("updating jetton {} information", v("address", it.address.toSafeBounceable())) }
            .subscribe {
                mono {
                    val jetton = JettonContract.of(it.address, liteApi)
                    val metadata = jetton.metadata()

                    val new = it.copy(
                        totalSupply = jetton.totalSupply,
                        mintable = jetton.mintable,
                        admin = jetton.admin,
                        name = metadata.name,
                        description = metadata.description,
                        symbol = metadata.symbol,
                        decimals = metadata.decimals,
                        image = metadata.image,
                        imageData = metadata.imageData ?: byteArrayOf(),
                        updated = Instant.now()
                    )
                    jettonRepository.update(new).awaitSingle()
                }
                    .timed()
                    .doOnNext {
                        registry.timer("service.scheduled.jetton")
                            .record(it.elapsed())
                    }
                    .subscribe()
            }
    }

    @Scheduled(initialDelay = "0s")
    internal fun run() {
    }

    companion object : KLogging()
}
