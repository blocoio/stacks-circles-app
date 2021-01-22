package org.stacks.app.data.network.services

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface HubService {

    //TODO: add auth bearer
    @POST("store/{btc-address}/profile.json")
    fun profile(@Body signedProfileToken: String): Flow<ResponseBody>

    //TODO: body change && add auth bearer
    @POST("store/{btc-address}/wallet-config.json")
    fun wallet(@Body signedProfileToken: String): Flow<ResponseBody>

}