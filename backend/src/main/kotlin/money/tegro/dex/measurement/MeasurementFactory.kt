package money.tegro.dex.measurement

import io.micrometer.core.instrument.Clock
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class MeasurementFactory {
    @Singleton
    fun meterRegistry(config: MeasurementConfig) = MeasurementRegistry(config, Clock.SYSTEM)

    @Singleton
    fun meterConfig(repository: MeasurementRepository) = MeasurementConfig(repository)
}
