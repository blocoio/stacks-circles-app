package io.bloco.circles.domain

import io.bloco.circles.data.IdentityModel
import io.bloco.circles.data.ProfileModel
import io.bloco.circles.data.interfaces.IdentityRepository
import kotlinx.coroutines.flow.first
import org.blockstack.android.sdk.toBtcAddress
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
    suspend fun create(username: String?): Result<IdentityModel> = try {
        var identities = identityRepository.observe().first()
        val keys = identityKeys.new()
        val btcAddress = keys.toBtcAddress()

        val newIdentity = generateIdentity.generate(btcAddress, username ?: btcAddress)

        identities = identities + newIdentity

        val profile = ProfileModel()
        username?.also {  registrarProfile.register(username, btcAddress) }
        uploadProfile.upload(profile, keys)
        uploadWallet.upload(identities)
        identityRepository.set(identities)

        Result.success(newIdentity)
    } catch (e: Throwable) {
        Result.failure(e)
    }

}
