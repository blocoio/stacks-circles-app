package org.stacks.app.data.network.services

import org.stacks.app.data.network.models.WalletConfig
import retrofit2.http.GET
import retrofit2.http.Path

interface GaiaService {

    @GET("hub/{bitcoin_address}/wallet-config.json")
    suspend fun wallet(@Path("bitcoin_address") address: String): WalletConfig

}