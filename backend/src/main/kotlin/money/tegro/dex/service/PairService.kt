package money.tegro.dex.service

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import money.tegro.dex.config.ServiceConfig
import money.tegro.dex.contract.PairContract
import money.tegro.dex.repository.PairRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments
import org.ton.block.AddrStd
import org.ton.lite.client.LiteClient

@Singleton
open class PairService(
    private val config: ServiceConfig,
    private val registry: MeterRegistry,

    private val liteClient: LiteClient,
    private val liveAccounts: Flow<AddrStd>,

    private val pairRepository: PairRepository,
) {
    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        runBlocking(Dispatchers.Default) {
            launch { run() }
        }
    }

    private suspend fun run() {
        merge(
            // Watch live
            liveAccounts
                .mapNotNull { pairRepository.findById(it) }
                .onEach {
                    registry.counter("service.pair.hits").increment()
                    logger.info("{} matched database entity", StructuredArguments.kv("address", it.address))
                },
            // Apart from watching live interactions, update them periodically
            channelFlow {
                while (currentCoroutineContext().isActive) {
                    logger.debug("running scheduled update of all database entities")
                    pairRepository.findAll().collect { send(it) }
                    kotlinx.coroutines.time.delay(config.pairPeriod)
                }
            }
        )
            .map {
                it.address to PairContract.getReserves(it.address as AddrStd, liteClient)
            }
            .collect {
                val (address, reserves) = it
                pairRepository.update(address, reserves.first, reserves.second)
            }
    }

    companion object : KLogging()
}
