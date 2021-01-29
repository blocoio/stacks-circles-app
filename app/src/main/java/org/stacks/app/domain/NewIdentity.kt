package org.stacks.app.domain

import kotlinx.coroutines.flow.first
import org.blockstack.android.sdk.toBtcAddress
import org.stacks.app.data.ProfileModel
import org.stacks.app.data.interfaces.IdentityRepository
import javax.inject.Inject

class NewIdentity
@Inject constructor(
    private val generateNewIdentityKeys: GenerateNewIdentityKeys,
    private val identityRepository: IdentityRepository,
    private val generateIdentity: GenerateIdentity,
    private val registrarProfile: RegistrarProfile,
    private val uploadProfile: UploadProfile,
    private val uploadWallet: UploadWallet,
) {
    suspend fun create(username: String): Result<Unit> = try {
        val keys = generateNewIdentityKeys.generate()
        val btcAddress = keys.toBtcAddress()
        val identities = identityRepository.observe().first().toMutableList()

        identities.add(generateIdentity.generate(btcAddress, username))

        val profile = ProfileModel()
        uploadProfile.upload(profile, keys)
        uploadWallet.upload(identities)
        registrarProfile.register(username, btcAddress)
        identityRepository.set(identities)

        Result.success(Unit)
    } catch (e: Throwable) {
        Result.failure(e)
    }

}
