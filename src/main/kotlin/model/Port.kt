package model

import kotlinx.serialization.*

//@JsonSerialize(using = ObjectAsStringSerializer::class)
@Serializable
data class Port(val hostPort: Int, val containerPort: Int) {
    override fun toString(): String {
        return "$hostPort:$containerPort"
    }

    @Serializer(forClass = Port::class)
    companion object : KSerializer<Port> {
        override fun serialize(encoder: Encoder, value: Port) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): Port {
            val (hostPort, containerPort) = decoder.decodeString().split(':')
            return Port(hostPort.toInt(), containerPort.toInt())
        }
    }
}
