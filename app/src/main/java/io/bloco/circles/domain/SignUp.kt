package io.bloco.circles.domain

import io.bloco.circles.data.IdentityModel
import io.bloco.circles.data.ProfileModel
import io.bloco.circles.data.interfaces.IdentityRepository
import org.blockstack.android.sdk.extensions.toBtcAddress
import org.blockstack.android.sdk.extensions.toStxAddress
import javax.inject.Inject

class SignUp
@Inject constructor(
    private val identityKeys: IdentityKeys,
    private val identityRepository: IdentityRepository,
    private val generateIdentity: GenerateIdentity,
    private val uploadProfile: UploadProfile,
    private val registrarProfile: RegistrarProfile,
    private val uploadWallet: UploadWallet,
) {
    suspend fun newAccount(username: String?): Result<IdentityModel> = try {
        val keys = identityKeys.new()
        val btcKeys = keys.toBtcAddress()
        val stxKeys = keys.toStxAddress(true)

        val profile = ProfileModel()
        val newIdentity = generateIdentity.generate(
            stxKeys,
            username
        )

        username?.also { unwrappedUsername ->
            registrarProfile.register(
                unwrappedUsername,
                btcKeys,
                stxKeys
            )
        }

        uploadProfile.upload(profile, keys)
        uploadWallet.upload(newIdentity)
        identityRepository.set(listOf(newIdentity))

        Result.success(newIdentity)
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
