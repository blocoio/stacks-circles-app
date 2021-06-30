 package io.bloco.circles.domain

import io.bloco.circles.data.EncryptedPreferencesIdentityRepository
import io.bloco.circles.data.EncryptedPreferencesSecretKeyRepository
import io.bloco.circles.data.IdentityModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.blockstack.android.sdk.model.BlockstackIdentity
import org.blockstack.android.sdk.toBtcAddress
import org.blockstack.android.sdk.toHexPublicKey64
import org.kethereum.bip32.generateChildKey
import org.kethereum.bip32.model.ExtendedKey
import org.kethereum.bip32.toKey
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.extensions.toHexStringNoPrefix
import org.kethereum.model.ECKeyPair
import org.komputing.kbip44.BIP44Element
import timber.log.Timber
import javax.inject.Inject

 class IdentityKeys
@Inject constructor(
    private val identityRepository: EncryptedPreferencesIdentityRepository,
    private val secretKeyRepository: EncryptedPreferencesSecretKeyRepository
) {

    //TODO: test size variants
    suspend fun new(): ECKeyPair {
        val identities = identityRepository.observe().first()
        val secretKey = secretKeyRepository.observe().first()

        return generateIdentityDataFromMnemonicWords(
            secretKey,
            (identities.size - 1).coerceAtLeast(0)
        ).keyPair
    }

    suspend fun from(identity: IdentityModel): ExtendedKey {
        val secretKey = secretKeyRepository.observe().first()
        val index =
            identityRepository.observe().first().indexOfFirst { it.username == identity.username }

        return generateIdentityDataFromMnemonicWords(secretKey, index)
    }

    private suspend fun generateIdentityDataFromMnemonicWords(
        seedPhrase: String,
        index: Int = 0
    ): ExtendedKey = withContext(Dispatchers.IO) {
        val words = MnemonicWords(seedPhrase)
        val identity = BlockstackIdentity(words.toSeed().toKey("m/888'/0'"))

        val keys = identity.identityKeys.generateChildKey(BIP44Element(true, index))

        val privateKey = keys.keyPair.privateKey.key.toHexStringNoPrefix()
        val publicKey = keys.keyPair.toHexPublicKey64()

        Timber.i("New Identity (i$index)")
        Timber.i("Address key: ${keys.keyPair.toBtcAddress()}")
        Timber.i("Private key: $privateKey")
        Timber.i("Public key: $publicKey")

        return@withContext keys
    }
}
