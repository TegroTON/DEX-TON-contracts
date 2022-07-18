package money.tegro.dex.service.watch

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import money.tegro.dex.source.LiveTransactionSource
import mu.KLogging

@Singleton
class SwapWatchService(
    private val transactionSource: LiveTransactionSource,
) {
    @Scheduled(initialDelay = "0s") // Set it up as soon as possible
    fun setup() {
        transactionSource
            .asFlux()
            .subscribe {}
    }

    companion object : KLogging()
}
