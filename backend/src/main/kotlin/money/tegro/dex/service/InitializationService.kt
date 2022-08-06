package money.tegro.dex.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micronaut.context.event.StartupEvent
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import money.tegro.dex.contract.PairContract
import money.tegro.dex.contract.TokenContract
import money.tegro.dex.contract.TokenMetadata
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.model.PairModel
import money.tegro.dex.model.TokenModel
import money.tegro.dex.repository.PairRepository
import money.tegro.dex.repository.TokenRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient
import java.io.ByteArrayInputStream
import java.net.URLConnection
import kotlin.coroutines.CoroutineContext

@Singleton
open class InitializationService(
    private val liteClient: LiteClient,
    private val resourceLoader: ClassPathResourceLoader,

    private val pairRepository: PairRepository,
    private val tokenRepository: TokenRepository,
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    @EventListener
    open fun onStartup(event: StartupEvent) {
    }

    @PostConstruct
    open fun onInit() {
        job.start()
    }

    @PreDestroy
    open fun onShutdown() {
        job.cancel()
    }

    private val job = launch {
        // For initialization, load pairs from pairs.json in the classpath
        resourceLoader.classLoader.getResource("pairs.json")
            ?.apply { logger.info("loading initial pairs from {}", kv("pairs", this)) }
            ?.readText()
            .let { jacksonObjectMapper().readTree(it) }
            .map {
                require(it.isObject)
                Triple(
                    AddrStd(it["pair"].asText()),
                    AddrStd(it["base"].asText()),
                    AddrStd(it["quote"].asText()),
                )
            }
            .asFlow()
            .filter { !pairRepository.existsById(it.first) }
            .collect {
                val (address, base, quote) = it

                for (token in listOf(base, quote)) {
                    logger.debug("ensuring token {}", kv("address", token.toSafeBounceable()))
                    if (!tokenRepository.existsById(token)) {
                        val data = TokenContract.of(token, liteClient)
                        val metadata = TokenMetadata.of(data.content)
                        tokenRepository.save(
                            TokenModel(
                                address = token,
                                supply = data.totalSupply,
                                mintable = data.mintable,
                                admin = data.admin,
                                name = metadata.name ?: "UNKNOWN TOKEN",
                                description = metadata.description ?: "Token without a description",
                                symbol = requireNotNull(metadata.symbol) { "Token ${token.toSafeBounceable()} defines no symbol" },
                                decimals = metadata.decimals,
                                image = metadata.image // Link to an off-chain image
                                    ?: metadata.imageData?.let { // On-chain image, converted to data url
                                        "data:" + (URLConnection.guessContentTypeFromStream(ByteArrayInputStream(it))
                                            ?: "image/png") + base64(it)
                                    }
                                    ?: DEFAULT_IMAGE // Data url with the default image
                            )
                        )
                    }
                }

                logger.debug(
                    "loading pair {} of {} and {}",
                    kv("address", address.toSafeBounceable()),
                    kv("base", base.toSafeBounceable()),
                    kv("quote", quote.toSafeBounceable())
                )
                val reserves = PairContract.getReserves(address, liteClient)
                pairRepository.save(
                    PairModel(
                        address = address,
                        base = base,
                        quote = quote,
                        // For ton, just pair address, otherwise query jetton wallet
                        baseWallet = if (tokenRepository.findById(base)?.symbol?.uppercase() == "TON") address
                        else TokenContract.getWalletAddress(
                            base,
                            address,
                            liteClient
                        ),
                        quoteWallet = TokenContract.getWalletAddress(quote, address, liteClient),
                        baseReserve = reserves.first,
                        quoteReserve = reserves.second,
                    )
                )
            }
    }

    companion object : KLogging() {
        const val DEFAULT_IMAGE =
            "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI5My45MzYiIGhlaWdodD0iOTMuOTM2IiBzdHlsZT0iZW5hYmxlLWJhY2tncm91bmQ6bmV3IDAgMCA5My45MzYgOTMuOTM2IiB4bWw6c3BhY2U9InByZXNlcnZlIj48cGF0aCBkPSJNODAuMTc5IDEzLjc1OGMtMTguMzQyLTE4LjM0Mi00OC4wOC0xOC4zNDItNjYuNDIyIDAtMTguMzQyIDE4LjM0MS0xOC4zNDIgNDguMDggMCA2Ni40MjEgMTguMzQyIDE4LjM0MiA0OC4wOCAxOC4zNDIgNjYuNDIyIDBzMTguMzQyLTQ4LjA4IDAtNjYuNDIxek00NC4xNDQgODMuMTE3Yy00LjA1NyAwLTcuMDAxLTMuMDcxLTcuMDAxLTcuMzA1IDAtNC4yOTEgMi45ODctNy40MDQgNy4xMDItNy40MDQgNC4xMjMgMCA3LjAwMSAzLjA0NCA3LjAwMSA3LjQwNCAwIDQuMzAxLTIuOTIgNy4zMDUtNy4xMDIgNy4zMDV6TTU0LjczIDQ0LjkyMWMtNC4xNSA0LjkwNS01Ljc5NiA5LjExNy01LjUwMyAxNC4wODhsLjA5NyAyLjQ5NWExLjA1IDEuMDUgMCAwIDEtMS4wNDUgMS4yMzloLTcuODY3YTEuMDUgMS4wNSAwIDAgMS0xLjA0Ny0uOTdsLS4yMDItMi42MjNjLS42NzYtNi4wODIgMS41MDgtMTIuMjE4IDYuNDk0LTE4LjIwMiA0LjMxOS01LjA4NyA2LjgxNi04Ljg2NSA2LjgxNi0xMy4xNDUgMC00LjgyOS0zLjAzNi03LjUzNi04LjU0OC03LjYyNC0zLjQwMyAwLTcuMjQyIDEuMTcxLTkuNTM0IDIuOTEzYTEuMDUgMS4wNSAwIDAgMS0xLjYxOC0uNDYzbC0yLjQyLTYuMzU0YTEuMDUyIDEuMDUyIDAgMCAxIC4zNjQtMS4yMjRjMy41MzgtMi41NzMgOS40NDEtNC4yMzUgMTUuMDQxLTQuMjM1IDEyLjM2IDAgMTcuODk0IDcuOTc1IDE3Ljg5NCAxNS44NzcgMCA3LjA3Mi0zLjg2NyAxMi4yMjYtOC45MjIgMTguMjI4eiIvPjwvc3ZnPg=="
    }
}
