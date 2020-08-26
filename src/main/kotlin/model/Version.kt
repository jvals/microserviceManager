package model

import kotlinx.serialization.*

//@JsonSerialize(using = ObjectAsStringSerializer::class)
@Serializable
data class Version(val major: Int, val minor: Int) {
    override fun toString(): String {
        return "$major.$minor"
    }

    @Serializer(forClass = Version::class)
    companion object : KSerializer<Version> {
        override fun serialize(encoder: Encoder, value: Version) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): Version {
            val (major, minor) = decoder.decodeString().split('.')
            return Version(major.toInt(), minor.toInt())
        }
    }
}