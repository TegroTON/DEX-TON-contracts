package money.tegro.dex.measurement

import io.micrometer.core.instrument.*
import io.micrometer.core.instrument.step.StepMeterRegistry
import io.micrometer.core.instrument.util.MeterPartition
import io.micrometer.core.instrument.util.NamedThreadFactory
import mu.KLogging
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.time.Instant
import java.util.concurrent.TimeUnit

// Largely inspired by https://medium.com/gathering-application-metrics-using-micrometer-and/gathering-metrics-using-micrometer-and-postgresql-2f51cbda883f
class MeasurementRegistry(private val config: MeasurementConfig, clock: Clock) : StepMeterRegistry(config, clock) {
    init {
        start(NamedThreadFactory("measurement-publisher"))
    }

    override fun getBaseTimeUnit(): TimeUnit = TimeUnit.MILLISECONDS

    override fun publish() {
        MeterPartition.partition(this, config.batchSize())
            .toFlux()
            .concatMap { it.toFlux() }
            .concatMap {
                it.match(
                    ::writeMeter,
                    ::writeMeter,
                    ::writeTimer,
                    ::writeSummary,
                    ::writeMeter,
                    ::writeMeter,
                    ::writeMeter,
                    ::writeFunctionTimer,
                    ::writeMeter,
                )
            }
            .subscribe {
                config.repository.save(it).subscribe()
            }
    }

    private fun writeMeter(m: Meter): Publisher<MeasurementModel> {
        val time = Instant.now()

        return m.measure()
            .toFlux()
            .map { ms ->
                MeasurementModel(
                    name = m.id.name,
                    dimension = config.dimension,
                    metadata = m.id.tags
                        .map { it.key to it.value }
                        .plus("statistic" to ms.statistic.tagValueRepresentation)
                        .toMap(),
                    value = ms.value,
                    timestamp = time,
                )
            }
    }

    private fun writeTimer(t: Timer): Publisher<MeasurementModel> {
        val time = Instant.now()

        return Flux.create {
            it.next(
                MeasurementModel(
                    name = t.id.name + ".sum",
                    dimension = config.dimension,
                    metadata = t.id.tags.map { it.key to it.value }.toMap(),
                    value = t.totalTime(baseTimeUnit),
                    timestamp = time,
                )
            )
            it.next(
                MeasurementModel(
                    name = t.id.name + ".count",
                    dimension = config.dimension,
                    metadata = t.id.tags.map { it.key to it.value }.toMap(),
                    value = t.count().toDouble(),
                    timestamp = time,
                )
            )
            it.next(
                MeasurementModel(
                    name = t.id.name + ".avg",
                    dimension = config.dimension,
                    metadata = t.id.tags.map { it.key to it.value }.toMap(),
                    value = t.mean(baseTimeUnit),
                    timestamp = time,
                )
            )
            it.next(
                MeasurementModel(
                    name = t.id.name + ".max",
                    dimension = config.dimension,
                    metadata = t.id.tags.map { it.key to it.value }.toMap(),
                    value = t.max(baseTimeUnit),
                    timestamp = time,
                )
            )
            it.complete()
        }
    }

    private fun writeSummary(s: DistributionSummary): Publisher<MeasurementModel> {
        val time = Instant.now()

        return Flux.create {
            it.next(
                MeasurementModel(
                    name = s.id.name + ".sum",
                    dimension = config.dimension,
                    metadata = s.id.tags.map { it.key to it.value }.toMap(),
                    value = s.totalAmount(),
                    timestamp = time,
                )
            )
            it.next(
                MeasurementModel(
                    name = s.id.name + ".count",
                    dimension = config.dimension,
                    metadata = s.id.tags.map { it.key to it.value }.toMap(),
                    value = s.count().toDouble(),
                    timestamp = time,
                )
            )
            it.next(
                MeasurementModel(
                    name = s.id.name + ".avg",
                    dimension = config.dimension,
                    metadata = s.id.tags.map { it.key to it.value }.toMap(),
                    value = s.mean(),
                    timestamp = time,
                )
            )
            it.next(
                MeasurementModel(
                    name = s.id.name + ".max",
                    dimension = config.dimension,
                    metadata = s.id.tags.map { it.key to it.value }.toMap(),
                    value = s.max(),
                    timestamp = time,
                )
            )
            it.complete()
        }
    }

    private fun writeFunctionTimer(t: FunctionTimer): Publisher<MeasurementModel> {
        val time = Instant.now()

        return Flux.create {
            it.next(
                MeasurementModel(
                    name = t.id.name + ".sum",
                    dimension = config.dimension,
                    metadata = t.id.tags.map { it.key to it.value }.toMap(),
                    value = t.totalTime(baseTimeUnit),
                    timestamp = time,
                )
            )
            it.next(
                MeasurementModel(
                    name = t.id.name + ".count",
                    dimension = config.dimension,
                    metadata = t.id.tags.map { it.key to it.value }.toMap(),
                    value = t.count(),
                    timestamp = time,
                )
            )
            it.next(
                MeasurementModel(
                    name = t.id.name + ".avg",
                    dimension = config.dimension,
                    metadata = t.id.tags.map { it.key to it.value }.toMap(),
                    value = t.mean(baseTimeUnit),
                    timestamp = time,
                )
            )
            it.complete()
        }
    }


    companion object : KLogging()
}
