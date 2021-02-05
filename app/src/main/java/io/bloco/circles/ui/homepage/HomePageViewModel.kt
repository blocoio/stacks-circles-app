package io.bloco.circles.ui.homepage

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.blockstack.android.sdk.Blockstack
import io.bloco.circles.domain.GetUserAuthState
import io.bloco.circles.ui.BaseViewModel
import io.bloco.circles.ui.homepage.HomePageViewModel.UserAuthState.Authenticated
import io.bloco.circles.ui.homepage.HomePageViewModel.UserAuthState.Unauthenticated
import timber.log.Timber

class HomePageViewModel
@ViewModelInject constructor(
    userAuthState: GetUserAuthState,
    blockstack: Blockstack
) : BaseViewModel() {

    // Outputs
    private val authenticatedState = MutableStateFlow(Authenticated)
    private val userAvatarImageUrl = BroadcastChannel<String?>(1)

    init {
        userAuthState
            .state()
            .onEach {
                when(it) {
                    is GetUserAuthState.UserAuthState.Authenticated -> {
                        authenticatedState.emit(Authenticated)
                        userAvatarImageUrl.send(blockstack.lookupProfile(it.mainIdentity.completeUsername!!, null).avatarImage)
                    }
                    GetUserAuthState.UserAuthState.Unauthenticated -> {
                        authenticatedState.emit(Unauthenticated)
                    }
                }
            }
            .catch { Timber.i("Can't access avatar image at this moment.") }
            .launchIn(ioScope)
    }

    // Outputs
    fun authenticatedState() =
        authenticatedState.asStateFlow()

    fun userAvatarImageUrl() =
        userAvatarImageUrl.asFlow()

    enum class UserAuthState {
        Authenticated, Unauthenticated
    }

}
