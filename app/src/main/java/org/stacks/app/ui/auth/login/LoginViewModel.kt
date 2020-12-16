package org.stacks.app.ui.auth.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.stacks.app.data.interfaces.IdentityRepository
import org.stacks.app.data.interfaces.SecretKeyRepository
import org.stacks.app.domain.Login
import org.stacks.app.ui.BaseViewModel

class LoginViewModel
@ViewModelInject constructor(
    login: Login,
    identityRepository: IdentityRepository,
    secretKeyRepository: SecretKeyRepository
) : BaseViewModel() {

    // Inputs
    private val secretKeyValues = MutableStateFlow("")
    private val submittedSecretKey = BroadcastChannel<String>(1)

    // Outputs
    private val secretKeyState = MutableStateFlow<LoginState>(LoginState.UpdatingSecretKey)

    init {
        secretKeyValues
            .asStateFlow()
            .onEach {
                secretKeyState.emit(
                    if (it.split(" ").count() > SECRET_KEY_WORDS) {
                        LoginState.InvalidSecretKey
                    } else {
                        LoginState.UpdatingSecretKey
                    }
                )
            }
            .launchIn(viewModelScope)

        submittedSecretKey
            .asFlow()
            .flatMapConcat { login.identitiesSecretKey(it) }
            .onEach {
                it.fold({ list ->
                    identityRepository.set(list)
                    secretKeyRepository.set(secretKeyValues.value)
                    secretKeyState.emit(LoginState.ValidSecretKey)
                }, {
                    secretKeyState.emit(LoginState.InvalidSecretKey)
                })
            }
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun secretKeyUpdated(privateKey: CharSequence) =
        secretKeyValues.emit(privateKey.toString())

    suspend fun submitSecretKey(text: String) =
        submittedSecretKey.send(text)

    // Outputs
    fun secretKeyState(): StateFlow<LoginState> =
        secretKeyState.asStateFlow()

    companion object {
        const val SECRET_KEY_WORDS = 12
    }

    sealed class LoginState {
        object InvalidSecretKey : LoginState()
        object UpdatingSecretKey : LoginState()
        object ValidSecretKey : LoginState()
    }

}
