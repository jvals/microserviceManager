package service

import model.Build
import model.ConnectionConf
import model.RunConfiguration
import model.RunConfigurations
import model.RunMode
import model.Service
import model.ServiceName

class DockerComposeService {

    companion object {
        fun applyRunConfiguration(
            inputServices: Map<ServiceName, Service>,
            runConfigurations: Map<ServiceName, RunConfiguration>
        ): Map<ServiceName, Service> {
            return inputServices
                .filterKeys {
                    runConfigurations.containsKey(it)
                            && runConfigurations[it]?.mode != RunMode.NOOP
                            && runConfigurations[it]?.mode != RunMode.EXTERNAL
                }
                .map { entry ->
                    entry.key to entry.value.copy(
                        build = if (runConfigurations[entry.key]?.mode == RunMode.BUILD) {
                            val context = runConfigurations[entry.key]?.context
                            val dockerfile = runConfigurations[entry.key]?.dockerfile
                            if (context != null) {
                                Build(context, dockerfile)
                            } else {
                                null
                            }
                        } else null,
                        image = if (runConfigurations[entry.key]?.mode == RunMode.BUILD) null else entry.value.image
                    )
                }.toMap()
        }

        fun applyConnectionConfiguration(
            inputService: Map<ServiceName, Service>,
            allConnections: Map<ServiceName, ConnectionConf>,
            externalServices: Set<ServiceName> = emptySet()
        ): Map<ServiceName, Service> {
            return inputService.map { services ->
                val newEnvironmentVariables = allConnections[services.key]?.let { connectionConf ->
                    connectionConf.connections.filter { connection ->
                        inputService.containsKey(connection.key) || externalServices.contains(connection.key)
                    }.map { connection ->
                        if (externalServices.contains(connection.key) && connection.value.externalConnections != null) {
                            connection.value.externalConnections!!.map { it.key to it.value }.toMap()
                        } else {
                            connection.value.internalConnections.map {
                                it.key to it.value
                            }.toMap()
                        }
                    }.fold(emptyMap<String, String>(), { acc, map ->
                        (acc.keys + map.keys).associateWith {
                            setOf(acc[it], map[it]).filterNotNull().joinToString(",")
                        }
                    })
                } ?: emptyMap()
                services.key to services.value.copy(
                    environment = services.value.environment?.mergeEnvironmentVariables(newEnvironmentVariables)
                        ?: newEnvironmentVariables
                )
            }.toMap()
        }

        fun getExternalServices(services: Map<ServiceName, Service>, runConfigurations: RunConfigurations): Set<ServiceName> =
            services.filterKeys { runConfigurations.runConfigurations[it]?.mode == RunMode.EXTERNAL }.keys

        private fun Map<String, String>.mergeEnvironmentVariables(newVariables: Map<String, String>): Map<String, String> {
            val mergedMap = this.toMutableMap()
            newVariables.forEach { (newKey: String, newValue: String) ->
                if (this.containsKey(newKey)) {
                    val mergedValue = "$newValue,${this.getValue(newKey)}"
                    mergedMap[newKey] = mergedValue
                } else {
                    mergedMap[newKey] = newValue
                }
            }

            return mergedMap.toMap()
        }
    }
}
