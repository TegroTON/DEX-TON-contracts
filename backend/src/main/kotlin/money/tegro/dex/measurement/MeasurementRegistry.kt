package money.tegro.dex.measurement

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.step.StepMeterRegistry
import io.micrometer.core.instrument.util.MeterPartition
import io.micrometer.core.instrument.util.NamedThreadFactory
import mu.KLogging
import java.util.concurrent.TimeUnit

class MeasurementRegistry(private val config: MeasurementConfig, clock: Clock) : StepMeterRegistry(config, clock) {
    init {
        start(NamedThreadFactory("measurement-publisher"))
    }

    override fun getBaseTimeUnit(): TimeUnit = TimeUnit.MILLISECONDS

    override fun publish() {
        logger.debug { "peep" }
        MeterPartition.partition(this, config.batchSize())
            .forEach { batch ->
                logger.debug { "peee pooooo" }
                batch.stream().forEach { meter ->
                    logger.debug { "poop" }
                    meter.measure().forEach { measurement ->
                        logger.debug { "fart, $measurement" }
                        config.repository.save(
                            MeasurementModel(
                                name = meter.id.name,
                                dimensions = config.dimension,
                                metadata = meter.id.tags.map { it.key to it.value }.toMap(),
                                value = measurement.value
                            )
                        ).subscribe()
                    }
                }
            }
    }

    companion object : KLogging()
}
