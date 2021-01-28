package org.stacks.app.domain

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.blockstack.android.sdk.Blockstack
import org.blockstack.android.sdk.model.BlockstackIdentity
import org.blockstack.android.sdk.model.CryptoOptions
import org.blockstack.android.sdk.toBtcAddress
import org.blockstack.android.sdk.toHexPublicKey64
import org.json.JSONArray
import org.json.JSONObject
import org.kethereum.bip32.generateChildKey
import org.kethereum.bip32.toKey
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.extensions.toHexString
import org.komputing.kbip44.BIP44Element
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.interfaces.SecretKeyRepository
import org.stacks.app.data.network.services.HubService
import javax.inject.Inject

class UploadWallet
@Inject constructor(
    private val secretKeyRepository: SecretKeyRepository,
    private val generateAuthToken: GenerateAuthToken,
    private val hubService: HubService,
    private val blockstack: Blockstack,
    private val gson: Gson
) {

    suspend fun upload(identityModel: IdentityModel) =
        upload(listOf(identityModel))

    suspend fun upload(identities: List<IdentityModel>) = withContext(Dispatchers.IO) {
        val secretKey = secretKeyRepository.observe().first()

        val words = MnemonicWords(secretKey)
        val identity = BlockstackIdentity(words.toSeed().toKey("m/"))

        val keys = identity.identityKeys.generateChildKey(BIP44Element(true, 45))

        val btcAddress = keys.keyPair.toBtcAddress()

        val arrayIdentities = JSONArray().apply {
            identities.forEach {
                put(it.json)
            }
        }

        val authToken = generateAuthToken.generate(keys.keyPair)

        blockstack.encryptContent(
            JSONObject().put("identities", arrayIdentities).toString(),
            CryptoOptions(
                keys.keyPair.toHexPublicKey64(),
                keys.keyPair.privateKey.key.toHexString()
            )
        ).value?.run {
            hubService.updateWallet(btcAddress, "bearer $authToken", this.json.toString())
        } ?: throw WalletEncryptionException()

    }

    class WalletEncryptionException : Exception("Failed to encrypt wallet")
}