package org.stacks.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.blockstack.android.sdk.model.BlockstackIdentity
import org.blockstack.android.sdk.toBtcAddress
import org.json.JSONException
import org.json.JSONObject
import org.kethereum.bip32.generateChildKey
import org.kethereum.bip32.model.ExtendedKey
import org.kethereum.bip32.toKey
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.extensions.toHexStringNoPrefix
import org.komputing.kbip44.BIP44Element
import org.stacks.app.shared.IdentitiesParsingFailed
import timber.log.Timber
import javax.inject.Inject

class Auth @Inject constructor() {

    private suspend fun generateIdentityDataFromMnemonicWords(seedPhrase: String, index: Int = 0) =
        withContext(Dispatchers.IO) {
            val words = MnemonicWords(seedPhrase)
            val identity = BlockstackIdentity(words.toSeed().toKey("m/888'/0'"))

            val keys = identity.identityKeys.generateChildKey(BIP44Element(true, index))

            val privateKey = keys.keyPair.privateKey.key.toHexStringNoPrefix()
            val publicKey = keys.keyPair.publicKey.key.toHexStringNoPrefix()

            Timber.i("Address key: ${keys.keyPair.toBtcAddress()}")
            Timber.i("Private key: $privateKey")
            Timber.i("Public key: $publicKey")

        }

    suspend fun generateWalletKeysFromMnemonicWords(seedPhrase: String): ExtendedKey =
        withContext(Dispatchers.IO) {
            val words = MnemonicWords(seedPhrase)
            val identity = BlockstackIdentity(words.toSeed().toKey("m/"))

            val key = identity.identityKeys.generateChildKey(BIP44Element(true, 45))

            Timber.i("Address key: ${key.keyPair.toBtcAddress()}")
            Timber.i("Private key: ${key.keyPair.privateKey.key.toHexStringNoPrefix()}")
            Timber.i("Public key: ${key.keyPair.publicKey.key.toHexStringNoPrefix()}")

            return@withContext key
        }

    /**
     * Gets the Identities present in the decoded cipher param, decoded cipher such as the Wallet-Config.json
     *
     * @param decodedCipher String - identities in JSON format
     *
     * @throws IdentitiesParsingFailed if it fails to convert the identities
     */
    fun identitiesFromDecodeCipher(decodedCipher: String): List<IdentityModel> {
        val listOfIdentities = mutableListOf<IdentityModel>()

        try {
            val identities = JSONObject(decodedCipher).getJSONArray("identities")

            for (i in 0 until identities.length()) {
                listOfIdentities.add(IdentityModel(identities.getJSONObject(i)))
            }
        } catch (e: JSONException) {
            Timber.w(e)
            throw IdentitiesParsingFailed(e)
        }

        return listOfIdentities
    }


    private fun getUserProfile(identity: IdentityModel) {
        throw NotImplementedError()
    }

    private fun registerNewIdentity(identity: IdentityModel) {
        throw NotImplementedError()
    }

    private fun generateNewHubAndPK(identity: IdentityModel) {
        throw NotImplementedError()
    }

    private fun updateUserProfile(identity: IdentityModel) {
        throw NotImplementedError()
    }

    private fun updateWalletConfig(identity: IdentityModel) {
        throw NotImplementedError()
    }

    private fun generateAuthResponse() {
        throw NotImplementedError()
    }

}
