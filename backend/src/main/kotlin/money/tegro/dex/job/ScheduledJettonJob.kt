package money.tegro.dex.job

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import money.tegro.dex.contract.JettonContract
import money.tegro.dex.model.JettonModel
import money.tegro.dex.repository.ExchangePairRepository
import money.tegro.dex.repository.JettonRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.v
import org.ton.lite.api.LiteApi
import reactor.kotlin.extra.bool.not
import java.time.Instant

@Singleton
class ScheduledJettonJob(
    private val liteApi: LiteApi,
    private val exchangePairRepository: ExchangePairRepository,
    private val jettonRepository: JettonRepository,
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        logger.info { "updating jetton information" }

        logger.info { "ensuring all exchange pairs have their jettons indexed" }
        exchangePairRepository.findAll()
            .flatMapIterable { listOfNotNull(it.left, it.right) }
            .filterWhen { jettonRepository.existsById(it).not() }
            .concatMap {
                mono {
                    logger.debug("adding missing jetton {} information", v("address", it))
                    val jetton = JettonContract.of(it, liteApi)
                    jettonRepository.save(
                        JettonModel(
                            address = it,
                            totalSupply = jetton.totalSupply,
                            mintable = jetton.mintable,
                            admin = jetton.admin,
                        )
                    ).awaitSingle()
                }
            }
            .blockLast()

        logger.info { "updating information of all jettons in the database" }
        jettonRepository.findAll()
            .concatMap {
                mono {
                    logger.debug("updating jetton {} information", v("address", it.address))
                    val jetton = JettonContract.of(it.address, liteApi)
                    val metadata = jetton.metadata()

                    val new = it.copy(
                        totalSupply = jetton.totalSupply,
                        mintable = jetton.mintable,
                        admin = jetton.admin,
                        name = metadata.name,
                        description = metadata.description,
                        symbol = metadata.symbol,
                        decimals = metadata.decimals,
                        image = metadata.image,
                        imageData = metadata.imageData ?: byteArrayOf(),
                        updated = Instant.now()
                    )
                    jettonRepository.update(new).awaitSingle()
                }
            }
            .blockLast()

        logger.info { "all done" }
    }

    companion object : KLogging()
}
