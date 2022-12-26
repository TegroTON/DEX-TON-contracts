package money.tegro.dex.tool

import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.Command

@Command(
    name = "tool",
    subcommands = [AddPairCommand::class]
)
class Application : Runnable {
    override fun run() {
        TODO("Not yet implemented")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PicocliRunner.run(Application::class.java, *args)
        }
    }
}
