package money.tegro.dex.service.scheduled

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.config.ScheduledServiceConfiguration
import money.tegro.dex.contract.JettonContract
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.model.JettonModel
import money.tegro.dex.repository.ExchangePairRepository
import money.tegro.dex.repository.JettonRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.kotlin.extra.bool.not
import java.time.Duration
import java.time.Instant

@Singleton
class ExchangePairJettonScheduledService(
    private val config: ScheduledServiceConfiguration,
    private val liteApi: LiteApi,
    private val exchangePairRepository: ExchangePairRepository,
    private val jettonRepository: JettonRepository,
) {
    @PostConstruct
    private fun setup() {
        Flux.interval(Duration.ZERO, config.exchangePairJettonUpdatePeriod)
            .concatMap { exchangePairRepository.findAll() }
            .concatMapIterable { listOfNotNull(it.left, it.right) }
            .filterWhen { jettonRepository.existsById(it).not() }
            .subscribe {
                mono {
                    logger.debug("adding missing jetton {} information", v("address", it.toSafeBounceable()))
                    val jetton = JettonContract.of(it, liteApi)
                    val metadata = jetton.metadata()

                    jettonRepository.save(
                        JettonModel(
                            address = it,
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
                    ).awaitSingle()
                }
                    .name("service.scheduled.exchangepair.jetton")
                    .tag("address", it.toSafeBounceable())
                    .metrics()
                    .subscribe()
            }
    }

    @Scheduled(initialDelay = "0s")
    internal fun run() {
    }

    companion object : KLogging()
}
