package model

import kotlinx.serialization.Serializable

@Serializable
data class Connection(val internalConnections: List<EnvironmentVar>, val externalConnections: List<EnvironmentVar>? = null)
