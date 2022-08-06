package money.tegro.dex.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable
import java.time.Duration

@ConfigurationProperties("dex.service")
interface ServiceConfig {
    @get:Bindable(defaultValue = "PT10M")
    val pairPeriod: Duration

    @get:Bindable(defaultValue = "PT1H")
    val tokenPeriod: Duration

    @get:Bindable(defaultValue = "PT1H")
    val walletPeriod: Duration
}
