package service

import model.Build
import model.ConnectionConf
import model.RunConfiguration
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
                    .filterKeys { runConfigurations.containsKey(it) && runConfigurations[it]?.mode != RunMode.NOOP }
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
                allConnections: Map<ServiceName, ConnectionConf>
        ): Map<ServiceName, Service> {
            return inputService.map { services ->
                val newEnvironmentVariables = allConnections[services.key]?.let { connectionConf ->
                    connectionConf.connections.filter { connection ->
                        inputService.containsKey(connection.key)
                    }.map { connection ->
                        connection.value.map {
                            it.key to it.value
                        }.toMap()
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
