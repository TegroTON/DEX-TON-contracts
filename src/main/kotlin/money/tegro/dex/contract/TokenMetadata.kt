package money.tegro.dex.contract

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.bitstring.BitString
import org.ton.cell.Cell
import org.ton.contract.*
import org.ton.crypto.sha256
import org.ton.tlb.loadTlb

data class TokenMetadata(
    val uri: String?,
    val name: String?,
    val description: String?,
    val image: String?,
    val imageData: ByteArray?,
    val symbol: String?,
    val decimals: Int = 9
) {
    companion object : KLogging() {
        private val mapper by lazy { jacksonObjectMapper() }

        val ONCHAIN_URI_KEY = BitString(sha256("uri".toByteArray()))
        val ONCHAIN_NAME_KEY = BitString(sha256("name".toByteArray()))
        val ONCHAIN_DESCRIPTION_KEY = BitString(sha256("description".toByteArray()))
        val ONCHAIN_IMAGE_KEY = BitString(sha256("image".toByteArray()))
        val ONCHAIN_IMAGE_DATA_KEY = BitString(sha256("image_data".toByteArray()))
        val ONCHAIN_SYMBOL_KEY = BitString(sha256("symbol".toByteArray()))
        val ONCHAIN_DECIMALS_KEY = BitString(sha256("decimals".toByteArray()))

        @JvmStatic
        suspend fun of(
            content: Cell,
            httpClient: HttpClient
        ): TokenMetadata {
            val full = content.parse { loadTlb(FullContent) }
            return when (full) {
                is FullContent.OnChain -> {
                    logger.debug { "on-chain content layout, frick" }
                    val entries = full.data.toMap()

                    TokenMetadata(
                        uri = entries.get(ONCHAIN_URI_KEY)?.flatten()?.decodeToString(), // TODO: Semi-on-chain content
                        name = entries.get(ONCHAIN_NAME_KEY)?.flatten()?.decodeToString(),
                        description = entries.get(ONCHAIN_DESCRIPTION_KEY)?.flatten()?.decodeToString(),
                        image = entries.get(ONCHAIN_IMAGE_KEY)?.flatten()?.decodeToString(),
                        imageData = entries.get(ONCHAIN_IMAGE_DATA_KEY)?.flatten(),
                        symbol = entries.get(ONCHAIN_SYMBOL_KEY)?.flatten()?.decodeToString(),
                        decimals = entries.get(ONCHAIN_DECIMALS_KEY)?.flatten()?.decodeToString()?.toInt() ?: 9,
                    )
                }
                is FullContent.OffChain -> {
                    logger.debug { "off-chain content layout, thanks god" }


                    val url = String(full.uri.data.flatten())
                    logger.debug("content url is {}", v("url", url))

                    val body = httpClient.get(url).bodyAsText()
                    mapper.readValue(body, TokenMetadata::class.java)
                }
            }
        }
    }
}

fun SnakeData.flatten(): ByteArray = when (this) {
    is SnakeDataTail -> bits.toByteArray()
    is SnakeDataCons -> bits.toByteArray() + next.flatten()
}

fun ContentData.flatten(): ByteArray = when (this) {
    is ContentData.Snake -> this.data.flatten()
    is ContentData.Chunks -> TODO("chunky content data")
}
