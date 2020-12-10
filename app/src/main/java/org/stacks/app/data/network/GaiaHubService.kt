package org.stacks.app.data.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface GaiaHubService {


    //TODO: this can backfire with older accounts, use the profile.
    // all we really need is the JWT token, with that we can get the rest
    // this is only useful if the name is still pending!!! still need to make some tests on this
    @GET("hub/{bitcoin_address}/wallet-config.json")
    fun wallet(@Path("bitcoin_address") address: String): Single<WalletConfig>

}