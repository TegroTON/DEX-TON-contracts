package money.tegro.dex.service

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.TaskScheduler
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.time.delay
import money.tegro.dex.config.ServiceConfig
import money.tegro.dex.contract.TokenContract
import money.tegro.dex.repository.TokenRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.client.LiteClient

@Singleton
open class TokenService(
    private val taskScheduler: TaskScheduler,
    private val config: ServiceConfig,
    private val registry: MeterRegistry,

    private val liteClient: LiteClient,
    private val liveAccounts: Flow<AddrStd>,

    private val tokenRepository: TokenRepository,
) {
    @EventListener
    open fun setup(event: StartupEvent) {
        runBlocking(Dispatchers.Default) {
            launch { run() }
        }
    }

    suspend fun run() {
        merge(
            // Watch live
            liveAccounts
                .mapNotNull { tokenRepository.findById(it) }
                .onEach {
                    registry.counter("service.token.hits").increment()
                    logger.info("{} matched database entity", kv("address", it.address))
                },
            // Apart from watching live interactions, update them periodically
            flow {
                while (currentCoroutineContext().isActive) {
                    logger.debug("running scheduled update of all database entities")
                    emitAll(tokenRepository.findAll())
                    delay(config.tokenPeriod)
                }
            }
        )
            .filter { it.symbol.uppercase() != "TON" }
            .map {
                it.address to TokenContract.of(it.address as AddrStd, liteClient)
            }
            .collect {
                val (address, token) = it
                tokenRepository.update(
                    address = address,
                    supply = token.totalSupply,
                    mintable = token.mintable,
                    admin = token.admin,
                )
            }
    }

    companion object : KLogging()
}
