package model

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer

@Serializable
data class ConnectionConfigurations(val connectionConfigurations: Map<ServiceName, ConnectionConf>) {
    // "connectionConfigurations" is not a "real" property. That is, it doesn't exist in the yml
    // structure. The yml structure only contains the map as its top level item.
    // The following logic ensures that the property "connectionConfigurations" is not
    // included in serialization and deserialization.
    @Serializer(forClass = ConnectionConfigurations::class)
    companion object : KSerializer<ConnectionConfigurations> {
        override fun serialize(encoder: Encoder, value: ConnectionConfigurations) {
            encoder.encodeSerializableValue(
                MapSerializer(
                    ServiceName.serializer(),
                    ConnectionConf.serializer()
                ),
                value.connectionConfigurations
            )
        }

        override fun deserialize(decoder: Decoder): ConnectionConfigurations {
            val configurations = decoder.decodeSerializableValue(
                MapSerializer(
                    ServiceName.serializer(),
                    ConnectionConf.serializer()
                )
            )
            return ConnectionConfigurations(configurations)
        }
    }
}