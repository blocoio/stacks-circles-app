package org.stacks.app.domain

import org.blockstack.android.sdk.toBtcAddress
import org.stacks.app.data.ProfileModel
import org.stacks.app.data.interfaces.IdentityRepository
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
    suspend fun newAccount(username: String): Result<Unit> = try {
        val keys = identityKeys.generate()

        val btcAddress = keys.toBtcAddress()

        val profile = ProfileModel()
        val identities = generateIdentity.generate(
            btcAddress,
            username
        )

        registrarProfile.register(username, btcAddress)
        uploadProfile.upload(profile, keys)
        uploadWallet.upload(identities)
        identityRepository.set(listOf(identities))

        Result.success(Unit)
    } catch (e: Throwable) {
        Result.failure(e)
    }
}