package model

import kotlinx.serialization.*

//@JsonSerialize(using = ObjectAsStringSerializer::class)
@Serializable
data class Image(val name: String) {
    override fun toString(): String {
        return name
    }

    @Serializer(forClass = Image::class)
    companion object : KSerializer<Image> {
        override fun serialize(encoder: Encoder, value: Image) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): Image {
            val name = decoder.decodeString()
            return Image(name)
        }
    }
}