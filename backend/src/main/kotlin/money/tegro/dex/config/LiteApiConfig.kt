package money.tegro.dex.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("money.tegro.dex.liteapi")
interface LiteApiConfig {
    @get:Bindable(defaultValue = "\${LITEAPI_IPV4:1426768764}")
    val ipv4: Int

    @get:Bindable(defaultValue = "\${LITEAPI_PORT:13724}")
    val port: Int

    @get:Bindable(defaultValue = "\${LITEAPI_KEY:`R1KsqYlNks2Zows+I9s4ywhilbSevs9dH1x2KF9MeSU=`}")
    val key: String
}
