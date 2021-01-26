package org.stacks.app.ui.auth.signup

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.blockstack.android.sdk.toBtcAddress
import org.stacks.app.data.ProfileModel
import org.stacks.app.domain.*
import org.stacks.app.domain.CheckUsernameStatus.UsernameStatus.Available
import org.stacks.app.ui.BaseViewModel
import timber.log.Timber

class ChooseUsernameViewModel
@ViewModelInject constructor(
    checkUsernameStatus: CheckUsernameStatus,
    generateNewIdentityKeys: GenerateNewIdentityKeys,
    generateIdentity: GenerateIdentity,
    uploadProfile: UploadProfile,
    registrarProfile: RegistrarProfile,
    uploadWallet: UploadWallet
) : BaseViewModel() {

    // Inputs
    private val usernameSubmitted = MutableStateFlow("")
    private val usernameValidated = BroadcastChannel<Unit>(1)

    // Outputs
    private val newAccount = BroadcastChannel<Unit>(1)
    private val errors = BroadcastChannel<Unit>(1)

    init {
        usernameSubmitted
            .filter { it.isNotEmpty() }
            .onEach {
                Timber.i("Username $it submitted")
            }
            .map { checkUsernameStatus.isAvailable(it) }
            .onEach {
                if (it == Available) {
                    Timber.i("${usernameSubmitted.value} is available")
                    usernameValidated.send(Unit)
                } else {
                    errors.send(Unit)
                }
            }
            .launchIn(ioScope)

        usernameValidated
            .asFlow()
            .flatMapConcat { generateNewIdentityKeys.generate() }
            .map { keys ->
                val username = usernameSubmitted.value
                val btcAddress = keys.toBtcAddress()

                val profile = ProfileModel()
                val identities = generateIdentity.generate(
                    btcAddress,
                    username
                )

                uploadProfile.upload(profile, keys)
                registrarProfile.register(username, btcAddress)
                uploadWallet.upload(identities, keys)
                newAccount.send(Unit)
            }
            .catch { e ->
                Timber.e(e)
                errors.send(Unit)
            }
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun submitUsername(username: String) = usernameSubmitted.emit(username)

    // Outputs
    fun newAccount() = newAccount.asFlow()
    fun errors() = errors.asFlow()

}