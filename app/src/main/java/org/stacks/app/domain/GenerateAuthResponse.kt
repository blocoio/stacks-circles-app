package org.stacks.app.domain

import kotlinx.coroutines.flow.first
import org.blockstack.android.sdk.Blockstack
import org.blockstack.android.sdk.Scope
import org.blockstack.android.sdk.model.BlockstackAccount
import org.blockstack.android.sdk.model.BlockstackIdentity
import org.json.JSONArray
import org.json.JSONObject
import org.kethereum.bip32.toKey
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.stacks.app.data.AuthRequestsStore
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.interfaces.SecretKeyRepository
import javax.inject.Inject

class GenerateAuthResponse
@Inject constructor(
    private val authRequestsStore: AuthRequestsStore,
    private val repository: SecretKeyRepository,
    private val updateProfile: UpdateProfile,
    private val updateWallet: UpdateWallet,
    private val identityKeys: IdentityKeys,
    private val blockstack: Blockstack,
) {

    //TODO: proper Exception
    suspend fun generate(identity: IdentityModel): String {
        val authRequest = authRequestsStore.get() ?: throw Exception()

        val words = MnemonicWords((repository.observe().first()))
        val salt = BlockstackIdentity(words.toSeed().toKey("m/888'/0'")).salt
        val keys = identityKeys.getFrom(identity)

        val account = BlockstackAccount(identity.completeUsername, keys, salt)

        updateWallet.update(authRequest, identity)
        updateProfile.update(authRequest.domainName, account)

        val payload = JSONObject().apply {
            put("public_keys", JSONArray().put(authRequest.publicKeys.first()))
            put("domain_name", authRequest.domainName)
        }

        val scopes = authRequest.scopes.map {
            Scope(it)
        }.toTypedArray()

        return blockstack.makeAuthResponse(
            payload, account,
            scopes
        )
    }
}
