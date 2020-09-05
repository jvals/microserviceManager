package model

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer

@Serializable
data class RunConfigurations(val runConfigurations: Map<ServiceName, RunConfiguration>) {
    // "runConfigurations" is not a "real" property. That is, it doesn't exist in the yml
    // structure. The yml structure only contains the map as its top level item.
    // The following logic ensures that the property "runConfigurations" is not
    // included in serialization and deserialization.
    @Serializer(forClass = RunConfigurations::class)
    companion object : KSerializer<RunConfigurations> {
        override fun serialize(encoder: Encoder, value: RunConfigurations) {
            encoder.encodeSerializableValue(
                MapSerializer(
                    ServiceName.serializer(),
                    RunConfiguration.serializer()
                ),
                value.runConfigurations
            )
        }

        override fun deserialize(decoder: Decoder): RunConfigurations {
            val configurations = decoder.decodeSerializableValue(
                MapSerializer(
                    ServiceName.serializer(),
                    RunConfiguration.serializer()
                )
            )
            return RunConfigurations(configurations)
        }
    }
}
