package money.tegro.market.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import money.tegro.dex.config.LiteApiConfig
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient

@Factory
class LiteClientFactory(private val config: LiteApiConfig) {
    @Singleton
    fun liteClient() = runBlocking {
        logger.debug(
            "attempting to connect to {} {} ({})",
            kv("ipv4", config.ipv4),
            kv("port", config.port),
            kv("key", config.key)
        )
        val lc = LiteClient {
            ipv4 = config.ipv4
            port = config.port
            publicKey = base64(config.key)
        }
        lc.start()
        logger.info(
            "lite client {} connected {}",
            kv("serverVersion", lc.serverVersion),
            kv("serverTime", lc.serverTime),
        )
        lc
    }

    companion object : KLogging()
}
