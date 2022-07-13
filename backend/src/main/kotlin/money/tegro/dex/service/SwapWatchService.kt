package money.tegro.dex.service

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import mu.KLogging

@Singleton
class SwapWatchService(
    private val transactionService: LiveTransactionService,
) {
    @Scheduled(initialDelay = "0s") // Set it up as soon as possible
    fun setup() {
        transactionService
            .asFlux()
            .subscribe {}
    }

    companion object : KLogging()
}
