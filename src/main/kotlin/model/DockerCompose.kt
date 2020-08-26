package model

import kotlinx.serialization.Serializable

@Serializable
data class DockerCompose(
    val version: Version,
    val services: Map<ServiceName, Service>,
    val networks: Networks? = null,
    val volumes: Volumes? = null
)