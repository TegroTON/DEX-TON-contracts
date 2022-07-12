package money.tegro.dex.contract

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.cell.Cell
import org.ton.crypto.sha256
import org.ton.smartcontract.*
import org.ton.tlb.loadTlb

data class JettonMetadata(
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

        @JvmStatic
        suspend fun of(
            content: Cell,
            httpClient: HttpClient
        ): JettonMetadata {
            val full = content.beginParse().loadTlb(FullContent)
            return when (full) {
                is FullContent.OnChain -> {
                    logger.debug { "on-chain content layout, frick" }
                    val entries = full.data.toMap().mapKeys { it.key.toByteArray() }

                    JettonMetadata(
                        uri = entries.get(sha256("uri".toByteArray()))?.flatten()
                            ?.decodeToString(), // TODO: Semi-on-chain content
                        name = entries.get(sha256("name".toByteArray()))?.flatten()?.decodeToString(),
                        description = entries.get(sha256("description".toByteArray()))?.flatten()?.decodeToString(),
                        image = entries.get(sha256("image".toByteArray()))?.flatten()?.decodeToString(),
                        imageData = entries.get(sha256("image_data".toByteArray()))?.flatten(),
                        symbol = entries.get(sha256("symbol".toByteArray()))?.flatten()?.decodeToString(),
                        decimals = entries.get(sha256("decimals".toByteArray()))?.flatten()?.decodeToString()?.toInt()
                            ?: 9
                    )
                }
                is FullContent.OffChain -> {
                    logger.debug { "off-chain content layout, thank's god" }


                    val url = String(full.uri.data.flatten())
                    logger.debug("content url is {}", v("url", url))

                    val body = httpClient.get(url).bodyAsText()
                    mapper.readValue(body, JettonMetadata::class.java)
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
