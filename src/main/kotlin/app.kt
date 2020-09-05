import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.CliktHelpFormatter
import command.BashCommand
import command.DockerComposeCommand

class App : CliktCommand() {
    init {
        context { helpFormatter = CliktHelpFormatter(showDefaultValues = true) }
    }

    override fun run(): Unit = Unit
}

fun main(args: Array<String>): Unit = App()
    .subcommands(DockerComposeCommand(), BashCommand())
    .main(args)