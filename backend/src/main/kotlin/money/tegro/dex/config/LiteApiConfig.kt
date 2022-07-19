package money.tegro.dex.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("money.tegro.dex.liteapi")
interface LiteApiConfig {
    @get:Bindable(defaultValue = "\${LITEAPI_IPV4:1097622104}")
    val ipv4: Int

    @get:Bindable(defaultValue = "\${LITEAPI_PORT:6345}")
    val port: Int

    @get:Bindable(defaultValue = "\${LITEAPI_KEY:`IzrzWCLuY6SMeYUeFzI7z7p/AEs6o8tXMTyqAZwMDyA=`}")
    val key: String
}
