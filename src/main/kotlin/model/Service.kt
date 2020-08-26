package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Serializable
data class Service(
    @SerialName("container_name") val containerName: String,
    val image: Image? = null,
    val build: Build? = null,
    val ports: List<Port>,
    @SerialName("depends_on") val dependsOn: List<Service>? = null,
    @SerialName("extra_hosts") val extraHosts: List<ExtraHost>? = null,
    val environment: Map<String, String>? = null,
    @SerialName("mem_limit") val memLimit: String
)
