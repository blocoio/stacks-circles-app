package org.stacks.app.domain

import com.colendi.ecies.EncryptedResult
import com.colendi.ecies.Encryption
import kotlinx.coroutines.flow.Flow
import org.blockstack.android.sdk.toBtcAddress
import org.stacks.app.data.Auth
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.network.GaiaHubService
import org.stacks.app.shared.mapIfSuccess
import org.stacks.app.shared.toResult
import javax.inject.Inject

class Login
@Inject constructor(
    private val gaiaHubService: GaiaHubService,
    private val auth: Auth
) {

    fun identitiesSecretKey(mnemonicWords: String): Flow<Result<List<IdentityModel>>> {
        val keys = auth.generateWalletKeysFromMnemonicWords(mnemonicWords)

        return gaiaHubService.wallet(keys.keyPair.toBtcAddress())
            .toResult()
            .mapIfSuccess { walletConfig ->
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

}

