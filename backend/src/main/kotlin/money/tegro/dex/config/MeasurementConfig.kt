package money.tegro.dex.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable
import java.time.Duration

@ConfigurationProperties("money.tegro.dex.measurement")
interface MeasurementConfig {
    @get:Bindable(defaultValue = "true")
    val enabled: Boolean

    @get:Bindable(defaultValue = "PT1M")
    val step: Duration
}
