package io.bloco.circles.data

data class AuthResponseModel(
    val appName:String,
    val redirectURL: String,
    val authResponseToken: String
)
