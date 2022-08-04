package money.tegro.dex.metric

import io.micrometer.core.instrument.*
import io.micrometer.core.instrument.step.StepMeterRegistry
import io.micrometer.core.instrument.util.MeterPartition
import io.micrometer.core.instrument.util.NamedThreadFactory
import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import mu.KLogging
import java.time.Instant
import java.util.concurrent.TimeUnit

// Largely inspired by https://medium.com/gathering-application-metrics-using-micrometer-and/gathering-metrics-using-micrometer-and-postgresql-2f51cbda883f
@Singleton
class MetricRegistry(
    private val repository: MetricRepository,
    private val config: MetricConfig
) :
    StepMeterRegistry(config, Clock.SYSTEM) {
    @PostConstruct
    fun setup() {
        start(NamedThreadFactory("metric-publisher"))
    }

    override fun getBaseTimeUnit(): TimeUnit = TimeUnit.MILLISECONDS

    @OptIn(FlowPreview::class)
    override fun publish() {
        runBlocking {
            MeterPartition.partition(this@MetricRegistry, config.batchSize())
                .asFlow()
                .flatMapConcat { it.asFlow() }
                .flatMapConcat {
                    when (it) {
                        is Gauge, is Counter, is LongTaskTimer, is TimeGauge, is FunctionCounter -> writeMeter(it)
                        is Timer -> writeTimer(it)
                        is DistributionSummary -> writeSummary(it)
                        is FunctionTimer -> writeFunctionTimer(it)
                        else -> TODO()
                    }
                }
                .collect {
                    repository.save(it)
                }
        }
    }

    private suspend fun writeMeter(m: Meter): Flow<MetricModel> {
        val time = Instant.now()

        return m.measure()
            .asFlow()
            .map { ms ->
                MetricModel(
                    name = m.id.name,
                    metadata = m.id.tags
                        .map { it.key to it.value }
                        .plus("statistic" to ms.statistic.tagValueRepresentation)
                        .toMap(),
                    value = ms.value,
                    timestamp = time,
                )
            }
    }

    private fun writeTimer(t: Timer): Flow<MetricModel> {
        val time = Instant.now()

        return flowOf(
            MetricModel(
                name = t.id.name + ".sum",
                metadata = t.id.tags.map { it.key to it.value }.toMap(),
                value = t.totalTime(baseTimeUnit),
                timestamp = time,
            ),
            MetricModel(
                name = t.id.name + ".count",
                metadata = t.id.tags.map { it.key to it.value }.toMap(),
                value = t.count().toDouble(),
                timestamp = time,
            ),
            MetricModel(
                name = t.id.name + ".avg",
                metadata = t.id.tags.map { it.key to it.value }.toMap(),
                value = t.mean(baseTimeUnit),
                timestamp = time,
            ),
            MetricModel(
                name = t.id.name + ".max",
                metadata = t.id.tags.map { it.key to it.value }.toMap(),
                value = t.max(baseTimeUnit),
                timestamp = time,
            )
        )
    }

    private fun writeSummary(s: DistributionSummary): Flow<MetricModel> {
        val time = Instant.now()

        return flowOf(
            MetricModel(
                name = s.id.name + ".sum",
                metadata = s.id.tags.map { it.key to it.value }.toMap(),
                value = s.totalAmount(),
                timestamp = time,
            ),
            MetricModel(
                name = s.id.name + ".count",
                metadata = s.id.tags.map { it.key to it.value }.toMap(),
                value = s.count().toDouble(),
                timestamp = time,
            ),
            MetricModel(
                name = s.id.name + ".avg",
                metadata = s.id.tags.map { it.key to it.value }.toMap(),
                value = s.mean(),
                timestamp = time,
            ),
            MetricModel(
                name = s.id.name + ".max",
                metadata = s.id.tags.map { it.key to it.value }.toMap(),
                value = s.max(),
                timestamp = time,
            )
        )
    }

    private fun writeFunctionTimer(t: FunctionTimer): Flow<MetricModel> {
        val time = Instant.now()

        return flowOf(
            MetricModel(
                name = t.id.name + ".sum",
                metadata = t.id.tags.map { it.key to it.value }.toMap(),
                value = t.totalTime(baseTimeUnit),
                timestamp = time,
            ),
            MetricModel(
                name = t.id.name + ".count",
                metadata = t.id.tags.map { it.key to it.value }.toMap(),
                value = t.count(),
                timestamp = time,
            ),
            MetricModel(
                name = t.id.name + ".avg",
                metadata = t.id.tags.map { it.key to it.value }.toMap(),
                value = t.mean(baseTimeUnit),
                timestamp = time,
            )
        )
    }

    companion object : KLogging()
}
