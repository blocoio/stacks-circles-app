package org.stacks.app.ui.homepage

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.blockstack.android.sdk.Blockstack
import org.stacks.app.data.interfaces.IdentityRepository
import org.stacks.app.ui.BaseViewModel
import org.stacks.app.ui.homepage.HomePageViewModel.UserAuthState.Authenticated
import org.stacks.app.ui.homepage.HomePageViewModel.UserAuthState.Unauthenticated
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
                        userAvatarImageUrl.send(blockstack.lookupProfile(it.mainIdentity.username!!, null).avatarImage)
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
