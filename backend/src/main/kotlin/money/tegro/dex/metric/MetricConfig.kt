package money.tegro.dex.metric

import io.micrometer.core.instrument.step.StepRegistryConfig
import io.micronaut.context.annotation.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("dex.metric")
class MetricConfig : StepRegistryConfig {
    var enabled: Boolean = true

    var step: Duration = Duration.ofSeconds(10)

    override fun prefix(): String = "metric"

    override fun get(key: String): String? = null

    override fun enabled(): Boolean = this.enabled

    override fun step(): Duration = this.step
}
