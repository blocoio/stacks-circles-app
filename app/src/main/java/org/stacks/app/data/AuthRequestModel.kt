package org.stacks.app.data

import com.google.gson.annotations.SerializedName

data class AuthRequestModel(
    val appDetails: AppDetails?,

    val client: String,

    @SerializedName("do_not_include_profile")
    val doNotIncludeProfile: Boolean,

    @SerializedName("domain_name")
    val domainName: String,

    val exp: Int,

    val iat: Int,

    val iss: String,

    val jti: String,
    @SerializedName("manifest_uri")
    val manifestUri: String,

    @SerializedName("public_keys")
    val publicKeys: List<String>,

    @SerializedName("redirect_uri")
    val redirectUri: String,

    val scopes: List<String>,

    val sendToSignIn: Boolean,

    @SerializedName("supports_hub_url")
    val supportsHubUrl: Boolean,

    val version: String
) {
    data class AppDetails(
        var icon: String,
        val name: String
    )
}