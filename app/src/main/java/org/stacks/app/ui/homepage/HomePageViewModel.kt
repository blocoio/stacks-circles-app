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
    identityRepository: IdentityRepository,
    blockstack: Blockstack
) : BaseViewModel() {

    // Outputs
    private val authenticatedState = MutableStateFlow(Authenticated)
    private val userAvatarImageUrl = BroadcastChannel<String?>(1)

    init {
        identityRepository
            .observe()
            .onEach {
                authenticatedState.emit(
                    if (it.isEmpty()) {
                        Unauthenticated
                    } else {
                        Authenticated
                    }
                )
            }
            .launchIn(ioScope)

        identityRepository
            .observe()
            .filter { it.isNotEmpty() }
            .onEach {
                userAvatarImageUrl.send(
                    blockstack.lookupProfile(
                        it.first().completeUsername!!,
                        null
                    ).avatarImage
                )
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
