package io.bloco.circles.domain

import io.bloco.circles.shared.decodeCockford32
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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
import pm.gnosis.utils.toHex

@ExperimentalUnsignedTypes
class AddressesTest {

    private val SEED_PHRASE = "float myth tuna chuckle estate recipe canoe equal sport matter zebra vanish pyramid this veteran oppose festival lava economy uniform open zoo shrug fade"
    private val PRIVATE_KEY = "9f6da87aa7a214d484517394ca0689a38faa8b3497bb9bf491bd82c31b5af79601"
    private val BTC_ADDRESS = "mxR2gXFFKs24XiPUxgXrHjt7WjY2Xir3Yn"
    private val STX_ADDRESS_TESTNET = "ST2WNPKGHNM1PKE1D95KGADR1X5MWXTJHDAYBBZPG"
    private val STX_ADDRESS_MAINNET = "SP2WNPKGHNM1PKE1D95KGADR1X5MWXTJHD8EJ1HHK"

    @Test
    fun stxAddressTest() = runBlocking {
        // Act
        val keys = generateWalletKeysFromMnemonicWords(SEED_PHRASE)

        println("STX ADDRESS: $STX_ADDRESS_MAINNET")
        println("Decoded STX ADDRESS: ${STX_ADDRESS_MAINNET.decodeCockford32().toHex()}")

        // Assert
        //Assert.assertEquals(keys.keyPair.privateKey.key.toHexStringNoPrefix(), PRIVATE_KEY)
        Assert.assertEquals(keys.keyPair.toBtcAddress(), BTC_ADDRESS)
    }

    private suspend fun generateWalletKeysFromMnemonicWords(seedPhrase: String)= withContext(Dispatchers.IO) {
        val words = MnemonicWords(seedPhrase)

        val stxKeys = BlockstackIdentity(words.toSeed().toKey("m/44'/5757'/0'/0"))
        //val identity = BlockstackIdentity(words.toSeed().toKey("m/888'/0'"))

        val keys = stxKeys.identityKeys.generateChildKey(BIP44Element(true, 0))

        val privateKey = keys.keyPair.privateKey.key.toHexStringNoPrefix()
        val publicKey = keys.keyPair.toHexPublicKey64()

        println("Address key: ${keys.keyPair.toBtcAddress()}")
        println("Private key: $privateKey")
        println("Public key: $publicKey")

        return@withContext keys
    }
}