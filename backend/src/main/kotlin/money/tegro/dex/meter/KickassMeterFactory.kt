package money.tegro.dex.meter

import io.micrometer.core.instrument.Clock
import io.micronaut.context.annotation.Factory
import io.r2dbc.spi.ConnectionFactory
import jakarta.inject.Singleton

@Factory
class KickassMeterFactory {
    @Singleton
    fun meterRegistry(config: KickassMeterConfig) = KickassMeterRegistry(config, Clock.SYSTEM)

    @Singleton
    fun meterConfig(connectionFactory: ConnectionFactory) = KickassMeterConfig(connectionFactory)
}
