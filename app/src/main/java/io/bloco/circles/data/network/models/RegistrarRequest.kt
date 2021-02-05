package io.bloco.circles.data.network.models

import com.google.gson.annotations.SerializedName

data class RegistrarRequest(
    val name: String,

    @SerializedName("owner_address")
    val ownerAddress: String,

    @SerializedName("zonefile")
    val zoneFile: String,
)
