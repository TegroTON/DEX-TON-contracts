package money.tegro.dex.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import money.tegro.dex.config.LiteApiConfig
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient

@Factory
class LiteApiFactory(private val config: LiteApiConfig) {
    @Singleton
    fun liteApi() = runBlocking {
        logger.debug(
            "attempting to connect to {} {} ({})",
            kv("ipv4", config.ipv4),
            kv("port", config.port),
            kv("key", config.key)
        )
        val lc = LiteClient(config.ipv4, config.port, base64(config.key)).connect()
        logger.info("lite client connected {}", kv("serverTime", lc.getTime().now))
        lc
    }

    companion object : KLogging()
}
