package org.stacks.app.domain

import com.colendi.ecies.EncryptedResult
import com.colendi.ecies.Encryption
import org.blockstack.android.sdk.model.BlockstackIdentity
import org.blockstack.android.sdk.toBtcAddress
import org.kethereum.bip32.generateChildKey
import org.kethereum.bip32.toKey
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.extensions.toHexStringNoPrefix
import org.komputing.kbip44.BIP44Element
import org.stacks.app.data.network.GaiaHubService
import timber.log.Timber
import javax.inject.Inject

class Auth
@Inject constructor(
    gaiaHubService: GaiaHubService
) {

    val SEED_PHRASE = ""

    init {
        val words = MnemonicWords(SEED_PHRASE)
        val identity = BlockstackIdentity(words.toSeed().toKey("m/"))

        val keys = identity.identityKeys.generateChildKey(BIP44Element(true, 45))

        val privateKey = keys.keyPair.privateKey.key.toHexStringNoPrefix()
        val publicKey = keys.keyPair.publicKey.key.toHexStringNoPrefix()

        Timber.i("Address key: ${keys.keyPair.toBtcAddress()}")
        Timber.i("Private key: $privateKey")
        Timber.i("Public key: $publicKey")

        val walletConfig = gaiaHubService.wallet(keys.keyPair.toBtcAddress()).blockingGet()

        Timber.i(" Cipher key ${walletConfig.cipherText}")

        val text = Encryption().decryptWithPrivateKey(
            EncryptedResult(
                walletConfig.ephemeralPK,
                walletConfig.iv,
                walletConfig.mac,
                walletConfig.cipherText
            ),
            keys.keyPair.privateKey.key
        )

        Timber.i(text.decodeToString())

    }

}