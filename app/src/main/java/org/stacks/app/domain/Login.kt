package org.stacks.app.domain

import com.colendi.ecies.EncryptedResult
import com.colendi.ecies.Encryption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
import org.stacks.app.data.EncryptedPreferencesIdentityRepository
import org.stacks.app.data.EncryptedPreferencesSecretKeyRepository
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.network.services.GaiaService
import org.stacks.app.shared.IdentitiesParsingFailed
import org.stacks.app.shared.mapIfSuccess
import org.stacks.app.shared.toResult
import timber.log.Timber
import javax.inject.Inject

class Login
@Inject constructor(
    private val gaiaService: GaiaService,
    private val identitiesRepo: EncryptedPreferencesIdentityRepository,
    private val secretKeyRepo: EncryptedPreferencesSecretKeyRepository
) {

    fun login(mnemonicWords: String): Flow<Result<List<IdentityModel>>> =
        flow { emit(generateWalletKeysFromMnemonicWords(mnemonicWords)) }
            .flatMapConcat { keys ->
                gaiaService.wallet(keys.keyPair.toBtcAddress())
                    .map { walletConfig ->
                        val text = Encryption().decryptWithPrivateKey(
                            EncryptedResult(
                                walletConfig.ephemeralPK,
                                walletConfig.iv,
                                walletConfig.mac,
                                walletConfig.cipherText
                            ),
                            keys.keyPair.privateKey.key
                        )

                        identitiesFromDecodeCipher(text.decodeToString())
                    }
            }
            .toResult()
            .mapIfSuccess {
                identitiesRepo.set(it)
                secretKeyRepo.set(mnemonicWords)
                it
            }

    private suspend fun generateWalletKeysFromMnemonicWords(seedPhrase: String): ExtendedKey =
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
    private fun identitiesFromDecodeCipher(decodedCipher: String): List<IdentityModel> {
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
}

