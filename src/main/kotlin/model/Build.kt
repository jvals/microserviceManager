package model

import kotlinx.serialization.Serializable

@Serializable
data class Build(val context: String, val dockerfile: String? = null)