package io.bloco.circles.data.network.services

import io.bloco.circles.data.network.models.RegistrarName
import io.bloco.circles.data.network.models.RegistrarResponse
import retrofit2.http.*

interface RegistrarService {

    @GET("v1/names/{name}")
    suspend fun lookupName(@Path("name") name: String): RegistrarName

    @Headers("Content-Type: application/json")
    @POST("/register")
    suspend fun register(@Body registrarRequest: String): RegistrarResponse
}
