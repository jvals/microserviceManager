package model

import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer

@Serializable
data class ConnectionConf(val connections: Map<ServiceName, List<EnvironmentVar>>) {
    @Serializer(forClass = ConnectionConf::class)
    companion object : KSerializer<ConnectionConf> {
        @ImplicitReflectionSerializer
        override fun serialize(encoder: Encoder, value: ConnectionConf) {
            encoder.encodeSerializableValue(MapSerializer<ServiceName, List<EnvironmentVar>>(
                    ServiceName.serializer(),
                    EnvironmentVar.serializer().list
            ), value.connections)
        }

        @ImplicitReflectionSerializer
        override fun deserialize(decoder: Decoder): ConnectionConf {
            val connectionConf = decoder.decodeSerializableValue(MapSerializer(
                    ServiceName.serializer(),
                    EnvironmentVar.serializer().list
            ))
            return ConnectionConf(connectionConf)
        }
    }
}
