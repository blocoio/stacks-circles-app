package org.stacks.app.data.network.services

import kotlinx.coroutines.flow.Flow
import org.stacks.app.data.network.models.WalletConfig
import retrofit2.http.GET
import retrofit2.http.Path

interface GaiaService {

    @GET("hub/{bitcoin_address}/wallet-config.json")
    fun wallet(@Path("bitcoin_address") address: String): Flow<WalletConfig>

}