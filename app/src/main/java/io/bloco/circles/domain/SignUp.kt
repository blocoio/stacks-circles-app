package io.bloco.circles.domain

import org.blockstack.android.sdk.toBtcAddress
import io.bloco.circles.data.IdentityModel
import io.bloco.circles.data.ProfileModel
import io.bloco.circles.data.interfaces.IdentityRepository
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
    suspend fun newAccount(username: String): Result<IdentityModel> = try {
        val keys = identityKeys.new()

        val btcAddress = keys.toBtcAddress()

        val profile = ProfileModel()
        val newIdentity = generateIdentity.generate(
            btcAddress,
            username
        )

        registrarProfile.register(username, btcAddress)
        uploadProfile.upload(profile, keys)
        uploadWallet.upload(newIdentity)
        identityRepository.set(listOf(newIdentity))

        Result.success(newIdentity)
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
