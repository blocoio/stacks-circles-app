package org.stacks.app.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.blockstack.android.sdk.model.BlockstackIdentity
import org.blockstack.android.sdk.toBtcAddress
import org.kethereum.bip32.generateChildKey
import org.kethereum.bip32.toKey
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.extensions.toHexStringNoPrefix
import org.kethereum.model.ECKeyPair
import org.komputing.kbip44.BIP44Element
import org.stacks.app.data.EncryptedPreferencesIdentityRepository
import org.stacks.app.data.EncryptedPreferencesSecretKeyRepository
import timber.log.Timber
import javax.inject.Inject

class GenerateNewIdentityKeys
@Inject constructor(
    private val identityRepository: EncryptedPreferencesIdentityRepository,
    private val secretKeyRepository: EncryptedPreferencesSecretKeyRepository
) {

    //TODO:
    // test size variants
    fun generate(): Flow<ECKeyPair> = identityRepository.observe()
        .take(1)
        .map { it.size - 1 }
        .map { if(it < 0) 0 else it }
        .zip(secretKeyRepository.observe().take(1)) { currentIndex, secretKey ->
            generateIdentityDataFromMnemonicWords(secretKey, currentIndex)
        }

    private suspend fun generateIdentityDataFromMnemonicWords(
        seedPhrase: String,
        index: Int = 0
    ): ECKeyPair = withContext(Dispatchers.IO) {
        val words = MnemonicWords(seedPhrase)
        val identity = BlockstackIdentity(words.toSeed().toKey("m/888'/0'"))

        val keys = identity.identityKeys.generateChildKey(BIP44Element(true, index))

        val privateKey = keys.keyPair.privateKey.key.toHexStringNoPrefix()
        val publicKey = keys.keyPair.publicKey.key.toHexStringNoPrefix()

        Timber.i("New Identity (i$index)")
        Timber.i("Address key: ${keys.keyPair.toBtcAddress()}")
        Timber.i("Private key: $privateKey")
        Timber.i("Public key: $publicKey")

        return@withContext keys.keyPair
    }

}