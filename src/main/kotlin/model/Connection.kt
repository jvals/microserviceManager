package model

import kotlinx.serialization.Serializable

@Serializable
data class Connection(val connectionName: String, val environmentVars: List<EnvironmentVar>)
