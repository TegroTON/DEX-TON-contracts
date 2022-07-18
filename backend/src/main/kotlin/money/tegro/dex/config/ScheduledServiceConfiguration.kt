package money.tegro.dex.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable
import java.time.Duration

@ConfigurationProperties("money.tegro.dex.service.scheduled")
interface ScheduledServiceConfiguration {
    @get:Bindable(defaultValue = "PT30M")
    val exchangePairJettonUpdatePeriod: Duration

    @get:Bindable(defaultValue = "PT12H")
    val exchangePairUpdatePeriod: Duration

    @get:Bindable(defaultValue = "PT10M")
    val jettonUpdatePeriod: Duration
}
