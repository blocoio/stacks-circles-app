package org.stacks.app.data.network.services

import okhttp3.ResponseBody
import org.stacks.app.data.network.models.HubInfo
import retrofit2.http.*

interface HubService {

    @GET("hub_info")
    suspend fun hubInfo() : HubInfo

    @Headers("Content-Type: application/json")
    @POST("store/{bitcoin_address}/profile.json")
    suspend fun updateProfile(@Path("bitcoin_address") address: String, @Header("Authorization") authHeader: String, @Body signedProfileToken: String): ResponseBody
    //TODO: body change && add auth bearer
    // WIP
    @POST("store/{btc-address}/wallet-config.json")
    suspend fun wallet(@Body signedProfileToken: String): ResponseBody

}