package org.stacks.app.ui.account

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.stacks.app.data.IdentityModel
import org.stacks.app.domain.GetUserAuthState
import org.stacks.app.domain.GetUserAuthState.UserAuthState.Authenticated
import org.stacks.app.domain.GetUserAuthState.UserAuthState.Unauthenticated
import org.stacks.app.domain.Logout
import org.stacks.app.shared.foldOnEach
import org.stacks.app.ui.BaseViewModel

class AccountViewModel
@ViewModelInject constructor(
    getUserAuthState: GetUserAuthState,
    logout: Logout
) : BaseViewModel() {

    // Inputs
    private val logoutPressed = BroadcastChannel<Unit>(1)

    // Outputs
    private val identities = BroadcastChannel<List<IdentityModel>>(1)
    private val loggedOut = BroadcastChannel<Unit>(1)
    private val errors = BroadcastChannel<Unit>(1)

    init {
        getUserAuthState
            .state()
            .onEach {
                when (it) {
                    is Authenticated -> identities.send(it.identities)
                    Unauthenticated -> errors.send(Unit)
                }
            }
            .launchIn(ioScope)

        logoutPressed
            .asFlow()
            .flatMapConcat { logout.logout() }
            .foldOnEach(
                {
                    loggedOut.send(Unit)
                },
                {
                    errors.send(Unit)
                }
            )
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun logoutPressed() = logoutPressed.send(Unit)

    // Outputs
    fun identities(): Flow<List<IdentityModel>> = identities.asFlow()
    fun errors(): Flow<Unit> = errors.asFlow()
    fun loggedOut(): Flow<Unit> = loggedOut.asFlow()

}
