package money.tegro.dex.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("money.tegro.dex.liteapi")
interface LiteApiConfig {
    @get:Bindable(defaultValue = "\${LITEAPI_IPV4:1959448750}")
    val ipv4: Int

    @get:Bindable(defaultValue = "\${LITEAPI_PORT:51281}")
    val port: Int

    @get:Bindable(defaultValue = "\${LITEAPI_KEY:`hyXd2d6yyiD/wirjoraSrKek1jYhOyzbQoIzV85CB98=`}")
    val key: String
}
