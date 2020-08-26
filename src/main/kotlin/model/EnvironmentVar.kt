package model

import kotlinx.serialization.*

@Serializable
data class EnvironmentVar(val key: String, val value: String) {
    override fun toString(): String {
        return "$key=$value"
    }

    @Serializer(forClass = EnvironmentVar::class)
    companion object : KSerializer<EnvironmentVar> {
        override fun serialize(encoder: Encoder, value: EnvironmentVar) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): EnvironmentVar {
            val (key, value) = decoder.decodeString().split('=')
            return EnvironmentVar(key, value)
        }
    }
}