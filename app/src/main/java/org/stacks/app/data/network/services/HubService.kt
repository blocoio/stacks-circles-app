package org.stacks.app.data.network.services

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import org.stacks.app.data.network.models.HubInfo
import retrofit2.http.*

interface HubService {


    @GET("hub_info")
    fun hubInfo() : Flow<HubInfo>

    @Headers("Content-Type: application/json")
    @POST("store/{bitcoin_address}/profile.json")
    fun updateProfile(@Path("bitcoin_address") address: String, @Header("Authorization") authHeader: String, @Body signedProfileToken: String): Flow<ResponseBody>

    //TODO: body change && add auth bearer
    // WIP
    @POST("store/{btc-address}/wallet-config.json")
    fun wallet(@Body signedProfileToken: String): Flow<ResponseBody>

}