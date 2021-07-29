package io.bloco.circles.domain

import com.colendi.ecies.EncryptedResult
import com.colendi.ecies.Encryption
import io.bloco.circles.data.EncryptedPreferencesIdentityRepository
import io.bloco.circles.data.EncryptedPreferencesSecretKeyRepository
import io.bloco.circles.data.IdentityModel
import io.bloco.circles.data.network.services.GaiaService
import io.bloco.circles.shared.IdentitiesParsingFailed
import io.bloco.circles.shared.forEach
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
import org.komputing.kbip44.BIP44Element
import timber.log.Timber
import javax.inject.Inject

class Login
@Inject constructor(
    private val gaiaService: GaiaService,
    private val identitiesRepo: EncryptedPreferencesIdentityRepository,
    private val secretKeyRepo: EncryptedPreferencesSecretKeyRepository
) {

    suspend fun login(mnemonicWords: String): Result<List<IdentityModel>> = try {
        val keys = generateWalletKeysFromMnemonicWords(mnemonicWords)
        val walletConfig = gaiaService.wallet(keys.keyPair.toBtcAddress())

        val text = Encryption().decryptWithPrivateKey(
            EncryptedResult(
                walletConfig.ephemeralPK,
                walletConfig.iv,
                walletConfig.mac,
                walletConfig.cipherText
            ),
            keys.keyPair.privateKey.key
        )

        val decodedText = text.decodeToString()
        print(decodedText)
        val identities = identitiesFromDecodeCipher(decodedText)

        identitiesRepo.set(identities)
        secretKeyRepo.set(mnemonicWords)

        Result.success(identities)
    } catch (e: Throwable) {
        Result.failure(e)
    }

    private suspend fun generateWalletKeysFromMnemonicWords(seedPhrase: String): ExtendedKey =
        withContext(Dispatchers.IO) {
            val words = MnemonicWords(seedPhrase)
            val identity = BlockstackIdentity(words.toSeed().toKey("m/"))

            return@withContext identity.identityKeys.generateChildKey(BIP44Element(true, 45))
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

            identities.forEach<JSONObject> {
                listOfIdentities.add(IdentityModel(it))
            }

        } catch (e: JSONException) {
            Timber.w(e)
            throw IdentitiesParsingFailed(e)
        }

        return listOfIdentities
    }
}

