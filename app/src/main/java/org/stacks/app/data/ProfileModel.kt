package org.stacks.app.data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.*

@Serializable
data class ProfileModel(
    @SerializedName("@type")
    val type: String = "Person",

    @SerializedName("@context")
    val context: String = "http://schema.org",

    val apps: Map<String, String> = emptyMap(),

    @Serializable(with = AppMetaData.AppMetaDataSerializer::class)
    val appsMeta: Map<String, AppMetaData> = emptyMap()
)

@Serializable
data class AppMetaData(
    val publicKey: String,
    val storage: String
) {
    @Serializer(forClass = AppMetaData::class)
    object AppMetaDataSerializer : KSerializer<AppMetaData> {
        override val descriptor: SerialDescriptor
            get() = SerialDescriptor("AppMetaData") {
                element("publicKey", PrimitiveDescriptor("publicKey", PrimitiveKind.STRING))
                element("storage", PrimitiveDescriptor("storage", PrimitiveKind.STRING))
            }

        override fun serialize(encoder: Encoder, value: AppMetaData) {
            encoder.encodeString(value.publicKey)
            encoder.encodeString(value.storage)
        }

        override fun deserialize(decoder: Decoder): AppMetaData {
            return AppMetaData(
                decoder.decodeString(),
                decoder.decodeString()
            )
        }
    }
}