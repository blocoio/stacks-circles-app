package io.bloco.circles.data.network.services

import io.bloco.circles.data.network.models.HubInfo
import retrofit2.http.*

interface HubService {

    @GET("hub_info")
    suspend fun hubInfo(): HubInfo

    @Headers("Content-Type: application/json")
    @POST("store/{bitcoin_address}/profile.json")
    suspend fun updateProfile(
        @Path("bitcoin_address") address: String,
        @Header("Authorization") authHeader: String,
        @Body signedProfileToken: String
    ): Unit

    @Headers("Content-Type: application/json")
    @POST("store/{bitcoin_address}/wallet-config.json")
    suspend fun updateWallet(
        @Path("bitcoin_address") address: String,
        @Header("Authorization") authHeader: String,
        @Body signedProfileToken: String
    ): Unit

}
