package io.bloco.circles.domain

import io.bloco.circles.shared.decodeCrockford32
import io.bloco.circles.shared.encodeCrockford32
import io.bloco.circles.shared.toStxAddress
import io.bloco.circles.shared.toTestNetStxAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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
import org.komputing.kbip44.BIP44Element

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
        // Arrange
        val keys = generateWalletKeysFromMnemonicWords(SEED_PHRASE)

        // Act / Assert
        assertEquals(PUBLIC_KEY, keys.keyPair.toHexPublicKey64())
        assertEquals(PRIVATE_KEY, keys.keyPair.privateKey.key.toHexStringNoPrefix())
        assertEquals(BTC_ADDRESS_MAINNET, keys.keyPair.toBtcAddress())
        assertEquals(STX_ADDRESS_MAINNET, "S${keys.keyPair.toStxAddress()}")
    }

    @Test
    fun stxAddressTestnetTest() = runBlocking {
        // Arrange
        val keys = generateWalletKeysFromMnemonicWords(SEED_PHRASE)

        // Act Assert
        assertEquals(STX_ADDRESS_TESTNET, "S${keys.keyPair.toTestNetStxAddress()}")
    }

    @Test
    fun crockford32Test() {
        val encoded = "something very very big and complex".encodeCrockford32()
        assertEquals("something very very big and complex", encoded.decodeCrockford32())
    }


    private suspend fun generateWalletKeysFromMnemonicWords(seedPhrase: String)= withContext(Dispatchers.IO) {
        val words = MnemonicWords(seedPhrase)
        val stxKeys = BlockstackIdentity(words.toSeed().toKey("m/44'/5757'/0'/0"))
        return@withContext stxKeys.identityKeys.generateChildKey(BIP44Element(false, 0))
    }
}