@file:OptIn(DelicateCoroutinesApi::class)

package money.tegro.dex.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import money.tegro.dex.config.LiteApiConfig
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.adnl.network.IPv4Address
import org.ton.api.liteserver.LiteServerDesc
import org.ton.api.pub.PublicKeyEd25519
import org.ton.crypto.base64
import org.ton.lite.api.LiteApiClient
import org.ton.lite.client.LiteClient

@Factory
class LiteApiFactory(private val config: LiteApiConfig) {
    @Singleton
    fun liteApi(): LiteApiClient {
        logger.debug(
            "attempting to connect to {} ({} {})",
            kv("host", IPv4Address(config.ipv4, config.port)),
            kv("ipv4", config.ipv4),
            kv("key", config.key)
        )
        val liteServerDesc = LiteServerDesc(
            ip = config.ipv4,
            port = config.port,
            id = PublicKeyEd25519(base64(config.key))
        )
        val lc = LiteClient(newSingleThreadContext("lite-client"), liteServerDesc)

        try {
            logger.debug("try get time")
            runBlocking {
                withTimeout(15_000) {
                    logger.debug("start get time")
                    logger.info("lite client connected {}", kv("serverTime", lc.getServerTime()))
                    logger.debug("end get time")
                }
            }
        } catch (e: Exception) {
            logger.debug("exception: $e")
            logger.error("failed to connect to lite client", e)
            throw e
        } finally {
            logger.debug("finally")
        }
        return lc.liteApi
    }

    companion object : KLogging()
}
