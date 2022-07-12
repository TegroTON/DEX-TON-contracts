package money.tegro.dex.service

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import org.ton.bitstring.BitString
import org.ton.block.AddrStd
import reactor.core.publisher.Sinks

@Singleton
class LiveAccountService(
    private val blockService: LiveBlockService,
) {
    private val sink: Sinks.Many<AddrStd> = Sinks.many().multicast().onBackpressureBuffer()

    init {
        setup()
    }
    
    fun asFlux() = sink.asFlux()

    private fun setup() =
        blockService.asFlux()
            .subscribe { block ->
                mono {// Processing each block asynchronously
                    block.extra.account_blocks.nodes()
                        .flatMap {
                            sequenceOf(AddrStd(block.info.shard.workchain_id, it.first.account_addr))
                                .plus(it.first.transactions.nodes().map {
                                    AddrStd(block.info.shard.workchain_id, it.first.account_addr)
                                })
                                .distinct()
                        }
                        .filter { it !in SYSTEM_ADDRESSES }
                        .forEach {
                            sink.emitNext(it, Sinks.EmitFailureHandler.FAIL_FAST) // TODO: more robust handler
                        }
                }.subscribe()
            }

    companion object {
        val SYSTEM_ADDRESSES = listOf(
            AddrStd(-1, BitString.of("5555555555555555555555555555555555555555555555555555555555555555")),
            AddrStd(-1, BitString.of("3333333333333333333333333333333333333333333333333333333333333333")),
            AddrStd(-1, BitString.of("0000000000000000000000000000000000000000000000000000000000000000")),
        )
    }
}
