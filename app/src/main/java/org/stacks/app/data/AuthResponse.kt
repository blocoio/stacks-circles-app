package org.stacks.app.data

data class AuthResponse(
    val appName:String,
    val redirectURL: String,
    val authResponseToken: String
)