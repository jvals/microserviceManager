package command

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import model.ConnectionConfigurations
import model.DockerCompose
import model.RunConfigurations
import model.Service
import model.ServiceName
import service.DockerComposeService
import java.io.File
import java.io.FileNotFoundException

class DockerComposeCommand : CliktCommand() {
    private val connectionConfigurationPath: String by option("-c", "--connection-config", help = "Path to connection configuration").default(standardConnectionConfigPath)
    private val dockerComposeInputPath: String by option("-d", "--docker-compose-blueprint", help = "Path to docker compose input").default(standardDockerComposeInputPath)
    private val output: String? by option("-o", "--output", help = "Write to file instead of stdout")
    private val runConfigurationPath: String by option("-r", "--run-config", help = "Path to run configuration").default(standardRunConfigPath)

    private val yamlConfiguration = YamlConfiguration(encodeDefaults = false)
    private val yaml = Yaml(configuration = yamlConfiguration)

    override fun run() {
        val dockerComposeInputRaw: String = readFileRequired(dockerComposeInputPath)
        val dockerComposeBlueprint: DockerCompose = yaml.parse(DockerCompose.serializer(), dockerComposeInputRaw)

        val runConfigurationsRaw: String = readFileRequired(runConfigurationPath)
        val runConfigurations = yaml.parse(RunConfigurations.serializer(), runConfigurationsRaw)

        val connectionConfigurationRaw: String? = readFileOptional(connectionConfigurationPath)
        val connectionConfigurations: ConnectionConfigurations? = if (connectionConfigurationRaw != null && connectionConfigurationRaw.isNotEmpty()) {
            yaml.parse(ConnectionConfigurations.serializer(), connectionConfigurationRaw)
        } else null

        val allKnownServices = dockerComposeBlueprint.services

        val runnableConnectedServices = allKnownServices
                .applyRunConfiguration(runConfigurations)
                .applyConnectionConfiguration(connectionConfigurations)

        val dockerComposeOutput = dockerComposeBlueprint.copy(
                services = runnableConnectedServices
        )

        val dockerComposeOutputRaw = yaml.stringify(DockerCompose.serializer(), dockerComposeOutput)

        output?.let {
            println("Writing to file $it")
            File(it).writeText(dockerComposeOutputRaw)
            println("Finished generating compose file in $it")
        } ?: println(dockerComposeOutputRaw)
    }

    private fun Map<ServiceName, Service>.applyRunConfiguration(runConfigurations: RunConfigurations): Map<ServiceName, Service> {
        return DockerComposeService.applyRunConfiguration(this, runConfigurations.runConfigurations)
    }

    private fun Map<ServiceName, Service>.applyConnectionConfiguration(connectionConfigurations: ConnectionConfigurations?): Map<ServiceName, Service> {
        // This method returns the map unmodified if connectionConfigurations is null
        return connectionConfigurations?.let {
            DockerComposeService.applyConnectionConfiguration(this, connectionConfigurations.connectionConfigurations)
        } ?: this
    }

    private fun readFileRequired(filePath: String): String {
        return try {
            File(filePath).readText()
        } catch (e: FileNotFoundException) {
            println("Unable to find file or directory $filePath")
            throw PrintHelpMessage(currentContext.command)
        }
    }

    private fun readFileOptional(filePath: String): String? {
        return try {
            File(filePath).readText()
        } catch (e: FileNotFoundException) {
            println("Unable to find file or directory $filePath, continuing without")
            null
        }
    }

    companion object {
        private const val standardDockerComposeInputPath = "config/dockerComposeBlueprint.yml"
        private const val standardRunConfigPath = "config/runConfig.yml"
        private const val standardConnectionConfigPath = "config/connectionConfig.yml"
    }
}
