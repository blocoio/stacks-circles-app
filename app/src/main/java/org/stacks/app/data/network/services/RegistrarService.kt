package org.stacks.app.data.network.services

import org.stacks.app.data.network.models.RegistrarName
import org.stacks.app.data.network.models.RegistrarRequest
import org.stacks.app.data.network.models.RegistrarResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RegistrarService {

    @GET("v1/names/{name}")
    suspend fun lookupName(@Path("name") name: String): RegistrarName

    @POST("/register")
    suspend fun register(@Body body: RegistrarRequest): RegistrarResponse
}