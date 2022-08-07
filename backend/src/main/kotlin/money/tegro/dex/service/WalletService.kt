package money.tegro.dex.service

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.time.delay
import money.tegro.dex.config.ServiceConfig
import money.tegro.dex.contract.TokenContract
import money.tegro.dex.contract.TokenWalletContract
import money.tegro.dex.contract.toSafeBounceable
import money.tegro.dex.model.WalletModel
import money.tegro.dex.repository.WalletRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.api.exception.TvmException
import org.ton.bigint.BigInt
import org.ton.block.AddrNone
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import java.math.BigInteger
import kotlin.coroutines.CoroutineContext

@Singleton
open class WalletService(
    private val config: ServiceConfig,
    private val registry: MeterRegistry,

    private val liteClient: LiteClient,
    private val liveAccounts: Flow<AddrStd>,

    private val walletRepository: WalletRepository,
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

    suspend fun getWallet(owner: MsgAddressInt, master: MsgAddress = AddrNone): WalletModel {
        walletRepository.findByOwnerAndMaster(owner, master)?.let {
            // Shortcuts, baby
            return it
        }

        if (master == AddrNone) {
            // Simple toncoins, will just return null if account is not active
            return walletRepository.save(
                WalletModel(
                    address = owner,
                    balance = liteClient.getAccount(LiteServerAccountId(owner as AddrStd))?.storage?.balance?.coins?.amount?.value
                        ?: BigInteger.ZERO, // If not found, just assume zero
                    owner = owner,
                    master = master,
                )
            )
        }

        if (master is MsgAddressInt) {
            // Token wallet
            // this call fails only when something is fishy, as internally it just computes state init
            // cannot do it on the server because it might have some extra logic implemented by the contract
            val address = TokenContract.getWalletAddress(master as AddrStd, owner, liteClient)
            return try {
                // If this call fails (uninitialized wallet, or something else), assume zero balance
                val data = TokenWalletContract.of(address as AddrStd, liteClient)
                require(data.owner == owner) // Sanity check
                require(data.jetton == master)
                walletRepository.save(
                    WalletModel(
                        address = address,
                        balance = data.balance,
                        owner = data.owner,
                        master = data.jetton,
                    )
                )
            } catch (e: TvmException) {
                walletRepository.save(
                    WalletModel(
                        address = address,
                        balance = BigInteger.ZERO,
                        owner = owner,
                        master = master,
                    )
                )
            }
        }

        throw IllegalStateException("shouldn't be here")
    }

    private val job = launch {
        merge(
            // Watch live
            liveAccounts
                .mapNotNull { walletRepository.findById(it) }
                .onEach {
                    registry.counter("service.wallet.hits").increment()
                    logger.info(
                        "{} matched database entity",
                        kv("address", (it.address as? AddrStd)?.toSafeBounceable() ?: it.address)
                    )
                },
            // Apart from watching live interactions, update them periodically
            channelFlow {
                while (currentCoroutineContext().isActive) {
                    logger.debug("running scheduled update of all database entities")
                    walletRepository.findAll().collect { send(it) }
                    delay(config.walletPeriod)
                }
            }
        )
            .collect {
                if (it.owner == it.address && it.master == AddrNone) { // TON balance
                    walletRepository.update(
                        it.address,
                        liteClient.getAccount(LiteServerAccountId(it.address as AddrStd))?.storage?.balance?.coins?.amount?.value
                            ?: BigInt.ZERO
                    )
                } else {
                    try {
                        val data = TokenWalletContract.of(it.address as AddrStd, liteClient)
                        walletRepository.update(
                            it.address,
                            data.balance,
                            data.owner,
                            data.jetton,
                        )
                    } catch (e: TvmException) {
                        // Can't update properly, just set balance to zero
                        walletRepository.update(it.address, BigInt.ZERO)
                    }
                }
            }
    }

    companion object : KLogging()
}
