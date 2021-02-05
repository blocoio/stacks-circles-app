package io.bloco.circles.data

import com.google.gson.annotations.SerializedName

data class ProfileModel(
    @SerializedName("@type")
    val type: String = "Person",

    @SerializedName("@context")
    val context: String = "http://schema.org",

    val apps: Map<String, String> = emptyMap(),

    val appsMeta: Map<String, AppMetaData> = emptyMap()
) {
    data class AppMetaData(
        val publicKey: String,
        val storage: String
    )
}
