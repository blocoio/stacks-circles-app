package org.stacks.app.data.network.services

import org.stacks.app.data.network.models.RegistrarName
import org.stacks.app.data.network.models.RegistrarResponse
import retrofit2.http.*

interface RegistrarService {

    @GET("v1/names/{name}")
    suspend fun lookupName(@Path("name") name: String): RegistrarName

    @Headers("Content-Type: application/json")
    @POST("/register")
    suspend fun register(@Body registrarRequest: String): RegistrarResponse
}