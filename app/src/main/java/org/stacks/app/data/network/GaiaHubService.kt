package org.stacks.app.data.network

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path

interface GaiaHubService {

    @GET("hub/{bitcoin_address}/wallet-config.json")
    fun wallet(@Path("bitcoin_address") address: String): Flow<WalletConfig>

}