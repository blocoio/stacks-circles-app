package org.stacks.app.domain

import com.colendi.ecies.EncryptedResult
import com.colendi.ecies.Encryption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.blockstack.android.sdk.toBtcAddress
import org.stacks.app.data.Auth
import org.stacks.app.data.EncryptedPreferencesIdentityRepository
import org.stacks.app.data.EncryptedPreferencesSecretKeyRepository
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.network.GaiaHubService
import org.stacks.app.shared.mapIfSuccess
import org.stacks.app.shared.toResult
import javax.inject.Inject

class Login
@Inject constructor(
    private val gaiaHubService: GaiaHubService,
    private val auth: Auth,
    private val identitiesRepo: EncryptedPreferencesIdentityRepository,
    private val secretKeyRepo: EncryptedPreferencesSecretKeyRepository
) {

    fun login(mnemonicWords: String): Flow<Result<List<IdentityModel>>> =
        flow { emit(auth.generateWalletKeysFromMnemonicWords(mnemonicWords)) }
            .flatMapConcat { keys ->
                gaiaHubService.wallet(keys.keyPair.toBtcAddress())
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

                        auth.identitiesFromDecodeCipher(text.decodeToString())
                    }
            }
            .toResult()
            .mapIfSuccess {
                identitiesRepo.set(it)
                secretKeyRepo.set(mnemonicWords)
                it
            }

}

