package model

import kotlinx.serialization.Serializable

@Serializable
data class RunConfiguration(val mode: RunMode, val context: String, val dockerfile: String? = null)
