package io.bloco.circles.shared

import me.uport.sdk.core.hexToByteArray
import org.blockstack.android.sdk.toHexPublicKey64
import org.kethereum.hashes.sha256
import org.kethereum.model.ECKeyPair
import org.komputing.khash.ripemd160.extensions.digestRipemd160
import org.komputing.khex.extensions.toNoPrefixHexString

fun ECKeyPair.toStxAddress(): String {
    val sha256 = toHexPublicKey64().hexToByteArray().sha256()
    val hash160 = sha256.digestRipemd160()
    val extended = "b0${hash160.toNoPrefixHexString()}"
    val cs = checksum("16${hash160.toNoPrefixHexString()}")
    return (extended + cs).hexToByteArray().encodeCrockford32()
}

fun ECKeyPair.toTestNetStxAddress() : String {
    val sha256 = toHexPublicKey64().hexToByteArray().sha256()
    val hash160 = sha256.digestRipemd160()
    val extended = "d0${hash160.toNoPrefixHexString()}"
    val cs = checksum("1a${hash160.toNoPrefixHexString()}")
    return (extended + cs).hexToByteArray().encodeCrockford32()
}

private fun checksum(extended: String): String {
    val checksum = extended.hexToByteArray().sha256().sha256()
    val shortPrefix = checksum.slice(0..3)
    return shortPrefix.toNoPrefixHexString()
}
