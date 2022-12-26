package money.tegro.dex

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import reactor.core.scheduler.Schedulers

@OpenAPIDefinition(
    info = Info(
        title = "DEX API",
        version = "0.0.1"
    )
)
object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("java.net.preferIPv4Stack", "true")
        Schedulers.enableMetrics()

        Micronaut.build(*args)
            .banner(false)
            .start()
    }
}
