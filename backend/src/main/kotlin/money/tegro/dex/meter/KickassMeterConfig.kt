package money.tegro.dex.meter

import io.micrometer.core.instrument.step.StepRegistryConfig
import io.r2dbc.spi.ConnectionFactory
import java.time.Duration

class KickassMeterConfig(
    val connectionFactory: ConnectionFactory,
    val dimension: Map<String, String> = mapOf("app" to "dex")
) :
    StepRegistryConfig {
    override fun prefix(): String = "meter"

    override fun get(key: String): String? = null

    override fun step(): Duration = Duration.ofSeconds(10)
}
