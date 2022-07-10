package money.tegro.dex.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("money.tegro.dex.liteapi")
class LiteApiConfig {
    var ipv4 = 1091947910
    var port = 7496
    var key = "EI32HF4Lr9mKSnw/dqiXQabpydo/FsyAPSwoeav4lbI="
}
