package money.tegro.dex.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable
import java.time.Duration

@ConfigurationProperties("money.tegro.dex.service.scheduled")
interface ScheduledServiceConfig {
    @get:Bindable(defaultValue = "PT30M")
    val exchangePairJettonUpdatePeriod: Duration

    @get:Bindable(defaultValue = "PT10M")
    val exchangePairUpdatePeriod: Duration

    @get:Bindable(defaultValue = "PT1H")
    val jettonUpdatePeriod: Duration
}
