package money.tegro.dex.meter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.step.StepMeterRegistry
import io.micrometer.core.instrument.util.MeterPartition
import io.micrometer.core.instrument.util.NamedThreadFactory
import io.r2dbc.spi.Connection
import mu.KLogging
import reactor.kotlin.core.publisher.toMono
import java.time.Instant
import java.util.concurrent.TimeUnit

class KickassMeterRegistry(private val config: KickassMeterConfig, clock: Clock) :
    StepMeterRegistry(config, clock) {
    init {
        start(NamedThreadFactory("kickass-metrics"))
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
                        config.connectionFactory.create().toMono()
                            .subscribe { connection: Connection ->
                                connection.createStatement(
                                    "INSERT INTO meter_measurements (timestamp, value, name, dimensions, metadata) VALUES ($1, $2, $3, json_object('{$4}'), json_object('{$5}'))"
                                ).bind(0, Instant.now())
                                    .bind(1, measurement.value)
                                    .bind(2, meter.id.name)
                                    .bind(3, jacksonObjectMapper().writeValueAsString(config.dimension))
                                    .bind(
                                        4,
                                        meter.id.tags.map { it.key to it.value }.toMap()
                                            .let { jacksonObjectMapper().writeValueAsString(it) })
                                    .execute()
                                    .toMono()
                                    .subscribe()
                            }
                    }
                }
            }
    }

    companion object : KLogging()
}
