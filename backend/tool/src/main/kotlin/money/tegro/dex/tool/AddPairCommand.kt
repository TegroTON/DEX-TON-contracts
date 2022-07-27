package money.tegro.dex.tool

import io.ktor.client.*
import jakarta.inject.Inject
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
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
import org.ton.lite.api.LiteApi
import picocli.CommandLine.*
import reactor.core.publisher.Flux
import reactor.kotlin.extra.bool.not

@Command(name = "add-pair", description = ["Validate and add a new exchange pair to the DB"])
class AddPairCommand : Runnable {
    @Parameters(paramLabel = "ADDRESS", description = ["Address of the exchange pair"])
    private lateinit var addressString: String

    @Option(names = ["--base"], description = ["Left token address. If not specified, defaults to native TON"])
    private var baseString: String? = null

    @Option(names = ["--quote"], description = ["Right token address"], required = true)
    private lateinit var quoteString: String


    @Inject
    private lateinit var liteApi: LiteApi

    @Inject
    private lateinit var pairRepository: PairRepository

    @Inject
    private lateinit var tokenRepository: TokenRepository

    override fun run() {
        runBlocking {
            val address = AddrStd(addressString)

            logger.info("Verifying exchange pair {}", kv("address", address.toSafeBounceable()))

            require(PairContract.isInitialized(address, liteApi)) {
                "Exchange pair is not yet properly initialized, cannot proceed"
            }

            // TODO: Base and Quote addresses will be possible to determine programmatically in the future
            // We'd get associated jetton wallets and query them to find their master contracts
            logger.info("Verifying its base and quote tokens")
            val base = baseString?.let { AddrStd(it) } ?: tokenRepository.findBySymbol("TON").awaitSingle().address

            val quote = AddrStd(quoteString)

            Flux.just(base, quote)
                .filterWhen { tokenRepository.existsById(it).not() }
                .collectList()
                .awaitSingle()
                .forEach {
                    logger.info("token {} is not in the DB", kv("address", it))

                    val data = TokenContract.of(it, liteApi)
                    logger.info("token data: {}", data)
                    val metadata = TokenMetadata.of(data.content, HttpClient {})
                    logger.info("token metadata: {}", data)

                    val model = TokenModel(
                        address = it,
                        supply = data.totalSupply,
                        mintable = data.mintable,
                        admin = data.admin,
                        name = metadata.name
                            ?: "New Token".apply { logger.warn { "Token does not specify its name, default is used - CHANGE IT MANUALLY" } },
                        description = metadata.description
                            ?: "".apply { logger.info { "Token does not have a description, it's recommended to add one manually later" } },
                        symbol = requireNotNull(metadata.symbol?.uppercase()) { "Tokens does not specify symbol" },
                        decimals = metadata.decimals,
                        image = metadata.image
                            ?: metadata.imageData?.let { "data:image/png;base64," + base64(it) }
                            ?: "".apply { logger.info { "Token does not have an icon specified" } }
                    )

                    logger.debug("token model: {}", model)
                    tokenRepository.save(model).awaitSingle()
                        .let { logger.info("{} was added to the DB", kv("address", it.address.toSafeBounceable())) }
                }


            val reserves = PairContract.getReserves(address, liteApi)
            logger.info("Pair LP reserves are {} and {}", kv("left", reserves.first), kv("right", reserves.second))

            // For ton, baseWallet = base address. This is done to avoid using nullable values as it fucks everything else up
            val baseWallet = if (base == tokenRepository.findBySymbol("TON").awaitSingle().address) base
            else TokenContract.getWalletAddress(base, address, liteApi)
            val quoteWallet = TokenContract.getWalletAddress(quote, address, liteApi)

            val model = PairModel(
                address = address,
                base = base,
                quote = quote,
                baseWallet = baseWallet,
                quoteWallet = quoteWallet,
                baseReserve = reserves.first,
                quoteReserve = reserves.second,
            )

            logger.debug("pair model: {}", model)
            pairRepository.save(model).awaitSingle()
                .let { logger.info("{} was added to the DB", kv("address", it.address.toSafeBounceable())) }
        }
    }

    companion object : KLogging()
}
