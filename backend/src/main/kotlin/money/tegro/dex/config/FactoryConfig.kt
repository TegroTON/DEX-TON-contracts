package money.tegro.dex.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable
import java.time.Duration

@ConfigurationProperties("dex.factory")
interface FactoryConfig {
    @get:Bindable(defaultValue = "PT10S")
    val liveBlockPeriod: Duration
}
