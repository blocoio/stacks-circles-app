package io.bloco.circles.domain

import io.bloco.circles.shared.decodeCrockford32
import io.bloco.circles.shared.encodeCrockford32
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.uport.sdk.core.hexToByteArray
import org.blockstack.android.sdk.model.BlockstackIdentity
import org.blockstack.android.sdk.toBtcAddress
import org.blockstack.android.sdk.toHexPublicKey64
import org.junit.Assert.assertEquals
import org.junit.Test
import org.kethereum.bip32.generateChildKey
import org.kethereum.bip32.toKey
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.extensions.toHexStringNoPrefix
import org.kethereum.hashes.sha256
import org.komputing.kbip44.BIP44Element
import org.komputing.khash.ripemd160.extensions.digestRipemd160
import org.komputing.khex.extensions.toNoPrefixHexString

class AddressesTest {

    private val SEED_PHRASE = "float myth tuna chuckle estate recipe canoe equal sport matter zebra vanish pyramid this veteran oppose festival lava economy uniform open zoo shrug fade"
    private val PRIVATE_KEY = "9f6da87aa7a214d484517394ca0689a38faa8b3497bb9bf491bd82c31b5af796" //01
    private val PUBLIC_KEY = "023064b1fa3c279cd7c8eca2f41c3aa33dc48741819f38b740975af1e8fef61fe4"
    private val BTC_ADDRESS_MAINNET = "1Hu5PUAGWqaokbusF7ZUTpfnejwKbAeGUd"
    private val STX_ADDRESS_MAINNET = "SP2WNPKGHNM1PKE1D95KGADR1X5MWXTJHD8EJ1HHK"

    // Test environment
    private val STX_ADDRESS_TESTNET = "ST2WNPKGHNM1PKE1D95KGADR1X5MWXTJHDAYBBZPG"
    // private val BTC_ADDRESS_TESTNET = "mxR2gXFFKs24XiPUxgXrHjt7WjY2Xir3Yn"

    @Test
    fun stxAddressMainnetTest() = runBlocking {
        // Act
        val keys = generateWalletKeysFromMnemonicWords(SEED_PHRASE)

        // Arrange
        val sha256 = keys.keyPair.toHexPublicKey64().hexToByteArray().sha256()
        val hash160 = sha256.digestRipemd160()
        val extended = "b0${hash160.toNoPrefixHexString()}"
        val cs = checksum("16${hash160.toNoPrefixHexString()}")
        val address = (extended + cs).hexToByteArray().encodeCrockford32()

        // Assert
        assertEquals(PUBLIC_KEY, keys.keyPair.toHexPublicKey64())
        assertEquals(PRIVATE_KEY, keys.keyPair.privateKey.key.toHexStringNoPrefix())
        assertEquals(BTC_ADDRESS_MAINNET, keys.keyPair.toBtcAddress())
        assertEquals(STX_ADDRESS_MAINNET, "S$address")
    }

    @Test
    fun stxAddressTestnetTest() = runBlocking {
        // Act
        val keys = generateWalletKeysFromMnemonicWords(SEED_PHRASE)

        // Arrange
        val sha256 = keys.keyPair.toHexPublicKey64().hexToByteArray().sha256()
        val hash160 = sha256.digestRipemd160()
        val extended = "d0${hash160.toNoPrefixHexString()}"
        val cs = checksum("1a${hash160.toNoPrefixHexString()}")
        val address = (extended + cs).hexToByteArray().encodeCrockford32()

        // Assert
        assertEquals(STX_ADDRESS_TESTNET, "S$address")
    }

    @Test
    fun crockford32Test() {
        val encoded = "something very very big and complex".encodeCrockford32()
        assertEquals("something very very big and complex", encoded.decodeCrockford32())
    }

    private fun checksum(extended: String): String {
        val checksum = extended.hexToByteArray().sha256().sha256()
        val shortPrefix = checksum.slice(0..3)
        return shortPrefix.toNoPrefixHexString()
    }

    private suspend fun generateWalletKeysFromMnemonicWords(seedPhrase: String)= withContext(Dispatchers.IO) {
        val words = MnemonicWords(seedPhrase)
        val stxKeys = BlockstackIdentity(words.toSeed().toKey("m/44'/5757'/0'/0"))
        return@withContext stxKeys.identityKeys.generateChildKey(BIP44Element(false, 0))
    }
}