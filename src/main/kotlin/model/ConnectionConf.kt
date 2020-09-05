package model

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer

@Serializable
data class ConnectionConf(val connections: Map<ServiceName, Connection>) {
    @Serializer(forClass = ConnectionConf::class)
    companion object : KSerializer<ConnectionConf> {
        @ImplicitReflectionSerializer
        override fun serialize(encoder: Encoder, value: ConnectionConf) {
            encoder.encodeSerializableValue(
                MapSerializer(
                    ServiceName.serializer(),
                    Connection.serializer()
                ), value.connections
            )
        }

        @ImplicitReflectionSerializer
        override fun deserialize(decoder: Decoder): ConnectionConf {
            val connections = decoder.decodeSerializableValue(
                MapSerializer(
                    ServiceName.serializer(),
                    Connection.serializer()
                )
            )
            return ConnectionConf(connections)
        }
    }
}
