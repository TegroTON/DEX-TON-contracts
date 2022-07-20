package money.tegro.dex.measurement

import io.micrometer.core.instrument.step.StepRegistryConfig
import java.time.Duration

class MeasurementConfig(
    val repository: MeasurementRepository,
    val dimension: Map<String, String> = mapOf("app" to "dex")
) : StepRegistryConfig {
    override fun prefix(): String = "measurement"

    override fun get(key: String): String? = null

    override fun step(): Duration = Duration.ofSeconds(10)
}
