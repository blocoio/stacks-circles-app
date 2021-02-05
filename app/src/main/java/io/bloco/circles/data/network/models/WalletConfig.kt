package io.bloco.circles.data.network.models

data class WalletConfig(
    val cipherText: String,
    val ephemeralPK: String,
    val iv: String,
    val mac: String,
    val wasString: Boolean
)
