package org.stacks.app.domain

import kotlinx.coroutines.flow.first
import org.blockstack.android.sdk.toBtcAddress
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.ProfileModel
import org.stacks.app.data.interfaces.IdentityRepository
import javax.inject.Inject

class NewIdentity
@Inject constructor(
    private val identityKeys: IdentityKeys,
    private val identityRepository: IdentityRepository,
    private val generateIdentity: GenerateIdentity,
    private val registrarProfile: RegistrarProfile,
    private val uploadProfile: UploadProfile,
    private val uploadWallet: UploadWallet,
) {
    suspend fun create(username: String): Result<IdentityModel> = try {
        var identities = identityRepository.observe().first()
        val keys = identityKeys.new()
        val btcAddress = keys.toBtcAddress()

        val newIdentity = generateIdentity.generate(btcAddress, username)

        identities = identities + newIdentity

        val profile = ProfileModel()
        registrarProfile.register(username, btcAddress)
        uploadProfile.upload(profile, keys)
        uploadWallet.upload(identities)
        identityRepository.set(identities)

        Result.success(newIdentity)
    } catch (e: Throwable) {
        Result.failure(e)
    }

}
