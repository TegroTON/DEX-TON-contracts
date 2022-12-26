package money.tegro.dex.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable
import java.time.Duration

@ConfigurationProperties("dex.factory")
interface FactoryConfig {
    /** Period between queries to the liteserver to see if new masterchain blocks are available */
    @get:Bindable(defaultValue = "PT5S")
    val liteBlockPeriod: Duration

    /** How many blocks can be in the processing queue */
    @get:Bindable(defaultValue = "256")
    val liteBlockQueue: Int

    /** For how long blocks are kept in memory. Longer is usually better as it ensures fewer blocks are queried twice */
    @get:Bindable(defaultValue = "PT10M")
    val liteBlockHistoryKeep: Duration

    /** Internal timeout for queries to block history. Less - more duplicates, more - slower processing */
    @get:Bindable(defaultValue = "PT0.2S")
    val liteBlockHistoryTimeout: Duration
}
