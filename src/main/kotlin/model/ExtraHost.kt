package model

import kotlinx.serialization.*

//@JsonSerialize(using = ObjectAsStringSerializer::class)
@Serializable
data class ExtraHost(val alias: String, val hostAddress: String) {
    override fun toString(): String {
        return "$alias:$hostAddress"
    }

    @Serializer(forClass = ExtraHost::class)
    companion object : KSerializer<ExtraHost> {
        override fun serialize(encoder: Encoder, value: ExtraHost) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): ExtraHost {
            val (alias, hostAddress) = decoder.decodeString().split(':')
            return ExtraHost(alias, hostAddress)
        }
    }
}
