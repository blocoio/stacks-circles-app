package io.bloco.circles.domain

import io.bloco.circles.shared.encodeCrockford32
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.uport.sdk.core.hexToByteArray
import org.blockstack.android.sdk.model.BlockstackIdentity
import org.blockstack.android.sdk.toBtcAddress
import org.blockstack.android.sdk.toHexPublicKey64
import org.junit.Assert
import org.junit.Test
import org.kethereum.bip32.generateChildKey
import org.kethereum.bip32.toKey
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.extensions.toHexStringNoPrefix
import org.komputing.kbip44.BIP44Element
import org.komputing.khash.ripemd160.extensions.digestRipemd160
import org.komputing.khash.sha256.extensions.sha256
import org.komputing.khex.extensions.toNoPrefixHexString

@ExperimentalUnsignedTypes
class AddressesTest {

    private val SEED_PHRASE = "float myth tuna chuckle estate recipe canoe equal sport matter zebra vanish pyramid this veteran oppose festival lava economy uniform open zoo shrug fade"
    private val PRIVATE_KEY = "9f6da87aa7a214d484517394ca0689a38faa8b3497bb9bf491bd82c31b5af79601"
    private val BTC_ADDRESS_MAINNET = "1Hu5PUAGWqaokbusF7ZUTpfnejwKbAeGUd"
    private val BTC_ADDRESS_TESTNET = "mxR2gXFFKs24XiPUxgXrHjt7WjY2Xir3Yn"
    private val STX_ADDRESS_TESTNET = "ST2WNPKGHNM1PKE1D95KGADR1X5MWXTJHDAYBBZPG"
    private val STX_ADDRESS_MAINNET = "SP2WNPKGHNM1PKE1D95KGADR1X5MWXTJHD8EJ1HHK"

    @Test
    fun stxAddressTest() = runBlocking {
        // Act
        val keys = generateWalletKeysFromMnemonicWords(SEED_PHRASE)

        // Assert
        val sha256 = keys.keyPair.toHexPublicKey64().hexToByteArray().sha256()
        val hash160 = sha256.digestRipemd160()
        val extended = "00${hash160.toNoPrefixHexString()}"
        val checksum = checksum(extended)
        val address = (extended + checksum).hexToByteArray().encodeCrockford32()

        Assert.assertEquals(keys.keyPair.toBtcAddress(), BTC_ADDRESS_MAINNET)
        Assert.assertEquals(STX_ADDRESS_MAINNET, "S$address")
    }

    private fun checksum(extended: String): String {
        val checksum = extended.hexToByteArray().sha256().sha256()
        val shortPrefix = checksum.slice(0..3)
        return shortPrefix.toNoPrefixHexString()
    }

    private suspend fun generateWalletKeysFromMnemonicWords(seedPhrase: String)= withContext(Dispatchers.IO) {
        val words = MnemonicWords(seedPhrase)

        val stxKeys = BlockstackIdentity(words.toSeed().toKey("m/44'/5757'/0'/0"))
        val keys = stxKeys.identityKeys.generateChildKey(BIP44Element(false, 0))

        val privateKey = keys.keyPair.privateKey.key.toHexStringNoPrefix()
        val publicKey = keys.keyPair.toHexPublicKey64()

        println("Address key: ${keys.keyPair.toBtcAddress()}")
        println("Private key: $privateKey")
        println("Public key: $publicKey")

        return@withContext keys
    }
}