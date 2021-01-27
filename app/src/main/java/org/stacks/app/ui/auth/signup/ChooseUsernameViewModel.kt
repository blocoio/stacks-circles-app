package org.stacks.app.ui.auth.signup

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.stacks.app.domain.CheckUsernameStatus
import org.stacks.app.domain.CheckUsernameStatus.UsernameStatus.Available
import org.stacks.app.domain.SignUp
import org.stacks.app.shared.foldOnEach
import org.stacks.app.ui.BaseViewModel
import timber.log.Timber

class ChooseUsernameViewModel
@ViewModelInject constructor(
    checkUsernameStatus: CheckUsernameStatus,
    signUp: SignUp
) : BaseViewModel() {

    // Inputs
    private val usernamePicked = MutableStateFlow("")

    // Outputs
    private val openNewAccountScreen = BroadcastChannel<Unit>(1)
    private val errors = BroadcastChannel<Unit>(1)

    init {
        usernamePicked
            .filter { it.isNotEmpty() }
            .map { checkUsernameStatus.isAvailable(it) }
            .onEach {
                if (it != Available) {
                    errors.send(Unit)
                }
            }
            .filter { it == Available }
            .map { signUp.newAccount(usernamePicked.value) }
            .foldOnEach(
                { openNewAccountScreen.send(Unit) }, //Success
                { e ->
                    Timber.e(e)
                    errors.send(Unit)
                } // Failure
            )
            .launchIn(ioScope)

    }

    // Inputs
    suspend fun usernamePicked(username: String) = usernamePicked.emit(username)

    // Outputs
    fun openNewAccountScreen() = openNewAccountScreen.asFlow()
    fun errors() = errors.asFlow()

}