package org.stacks.app.ui.auth.signup

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.stacks.app.domain.CheckUsernameStatus
import org.stacks.app.domain.CheckUsernameStatus.UsernameStatus.Available
import org.stacks.app.domain.NewIdentity
import org.stacks.app.domain.SignUp
import org.stacks.app.shared.foldOnEach
import org.stacks.app.ui.BaseViewModel
import org.stacks.app.ui.auth.signup.ChooseUsernameViewModel.Errors.SignUpError
import org.stacks.app.ui.auth.signup.ChooseUsernameViewModel.Errors.UnavailableUsername
import timber.log.Timber

class ChooseUsernameViewModel
@ViewModelInject constructor(
    checkUsernameStatus: CheckUsernameStatus,
    signUp: SignUp,
    newIdentity: NewIdentity
) : BaseViewModel() {

    private var newAccount: Boolean = false

    // Inputs
    private val usernameSubmitted = MutableStateFlow("")

    // Outputs
    private val openNewAccountScreen = BroadcastChannel<Unit>(1)
    private val loading = MutableStateFlow(false)
    private val errors = BroadcastChannel<Errors>(1)

    init {
        usernameSubmitted
            .filter { it.isNotEmpty() && loading.value }
            .map {
                loading.emit(true)
                checkUsernameStatus.isAvailable(it)
            }
            .onEach {
                if (it != Available) {
                    loading.emit(false)
                    errors.send(UnavailableUsername)
                }
            }
            .filter { it == Available }
            .map {
                if (newAccount) {
                    signUp.newAccount(usernameSubmitted.value)
                } else {
                    newIdentity.create(usernameSubmitted.value)
                }
            }
            .foldOnEach(
                { openNewAccountScreen.send(Unit) }, //Success
                { e ->
                    Timber.e(e)
                    loading.emit(false)
                    errors.send(SignUpError)
                } // Failure
            )
            .launchIn(ioScope)

    }

    // Inputs
    suspend fun usernamePicked(username: String, signUp: Boolean) {
        newAccount = signUp
        usernameSubmitted.emit(username)
    }

    // Outputs
    fun openNewAccountScreen() = openNewAccountScreen.asFlow()
    fun loading() = loading.asStateFlow()
    fun errors() = errors.asFlow()

    enum class Errors {
        UnavailableUsername, SignUpError
    }

}