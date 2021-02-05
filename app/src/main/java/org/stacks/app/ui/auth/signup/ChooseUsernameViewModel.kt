package org.stacks.app.ui.auth.signup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.stacks.app.data.AuthRequestsStore
import org.stacks.app.data.AuthResponseModel
import org.stacks.app.domain.*
import org.stacks.app.domain.CheckUsernameStatus.UsernameStatus.Available
import org.stacks.app.shared.foldOnEach
import org.stacks.app.ui.BaseViewModel
import org.stacks.app.ui.auth.signup.ChooseUsernameViewModel.Error.*
import timber.log.Timber

class ChooseUsernameViewModel
@ViewModelInject constructor(
    generateAuthResponse: GenerateAuthResponse,
    checkUsernameStatus: CheckUsernameStatus,
    authRequestsStore: AuthRequestsStore,
    getAppDetails: GetAppDetails,
    newIdentity: NewIdentity,
    signUp: SignUp,
) : BaseViewModel() {

    // Variables
    private var isAuthRequest: Boolean = authRequestsStore.get() != null

    // Inputs
    private val usernameSubmitted = MutableStateFlow("")
    var signUp: Boolean = false

    // Outputs
    private val sendAuthResponse = BroadcastChannel<AuthResponseModel>(1)
    private val openNewAccountScreen = BroadcastChannel<Unit>(1)
    private val loading = MutableStateFlow(false)
    private val errors = BroadcastChannel<Error>(1)

    init {

        usernameSubmitted
            .filter { it.isEmpty() || it.contains(" ") }
            .onEach { errors.send(InvalidUsername) }
            .launchIn(viewModelScope)

        usernameSubmitted
            .filter { it.isNotEmpty() && !it.contains(" ") && !loading.value }
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
                if (this.signUp) {
                    signUp.newAccount(usernameSubmitted.value)
                } else {
                    newIdentity.create(usernameSubmitted.value)
                }
            }
            .foldOnEach(
                {
                    if (isAuthRequest) {
                        val authRequest = authRequestsStore.get()!!

                        sendAuthResponse.send(
                            AuthResponseModel(
                                getAppDetails.get(authRequest)!!.name,
                                authRequest.redirectUri,
                                generateAuthResponse.generate(it)
                            )
                        )
                    } else {
                        openNewAccountScreen.send(Unit)
                    }
                }, //Success
                { e ->
                    Timber.e(e)
                    loading.emit(false)
                    errors.send(SignUpError)
                } // Failure
            )
            .launchIn(ioScope)

    }

    // Inputs
    suspend fun usernamePicked(username: String) = usernameSubmitted.emit(username)

    // Outputs
    fun sendAuthResponse(): Flow<AuthResponseModel> = sendAuthResponse.asFlow()
    fun openNewAccountScreen() = openNewAccountScreen.asFlow()
    fun loading() = loading.asStateFlow()
    fun errors() = errors.asFlow()

    enum class Error {
        UnavailableUsername, SignUpError, InvalidUsername
    }

}