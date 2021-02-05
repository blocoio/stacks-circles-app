package io.bloco.circles.data.network.services

import okhttp3.ResponseBody
import io.bloco.circles.data.network.models.WalletConfig
import retrofit2.http.GET
import retrofit2.http.Path

interface GaiaService {

    @GET("hub/{bitcoin_address}/wallet-config.json")
    suspend fun wallet(@Path("bitcoin_address") address: String): WalletConfig

    @GET("hub/{bitcoin_address}/profile.json")
    suspend fun profile(@Path("bitcoin_address") address: String): ResponseBody

}
