package money.tegro.dex.contract

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.cell.Cell
import org.ton.smartcontract.SnakeDataTail
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
            val cs = content.beginParse()
            return when (val contentLayout = cs.loadUInt(8).toInt()) {
                0x00 -> {
                    logger.debug { "on-chain content layout, frick" }
                    TODO("FUCKFUCKFUCK")
                }
                0x01 -> {
                    logger.debug { "off-chain content layout, thank's god" }

                    val rawData =
                        cs.loadTlb(SnakeDataTail.tlbCodec()) // TODO: Only tails work for now but usually it's good enough
                    cs.endParse()

                    val url = String(rawData.bits.toByteArray())
                    logger.debug("content url is {}", v("url", url))

                    val body = httpClient.get(url).bodyAsText()
                    mapper.readValue(body, JettonMetadata::class.java)
                }
                else -> {
                    throw Exception("unknown content layout $contentLayout, cannot proceed")
                }
            }
        }
    }
}
