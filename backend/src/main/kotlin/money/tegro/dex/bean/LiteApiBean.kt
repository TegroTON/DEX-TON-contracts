package money.tegro.dex.bean

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import money.tegro.dex.config.LiteApiConfig
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient

@Factory
class LiteApiBean(private val config: LiteApiConfig) {
    @Singleton
    fun liteApi() = runBlocking {
        LiteClient(config.ipv4, config.port, base64(config.key)).connect()
    }
}
