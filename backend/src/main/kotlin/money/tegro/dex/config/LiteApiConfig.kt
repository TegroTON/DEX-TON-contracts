package money.tegro.dex.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("money.tegro.dex.liteapi")
class LiteApiConfig {
    var ipv4 = 1959448750
    var port = 51281
    var key = "hyXd2d6yyiD/wirjoraSrKek1jYhOyzbQoIzV85CB98="
}
