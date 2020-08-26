package model

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class ServiceName(val name: String) {
    @Serializer(forClass = ServiceName::class)
    companion object : KSerializer<ServiceName> {
        override fun serialize(encoder: Encoder, value: ServiceName) {
            encoder.encodeString(value.name)
        }

        override fun deserialize(decoder: Decoder): ServiceName {
            val name = decoder.decodeString()
            return ServiceName(name)
        }
    }
}
