package org.stacks.app.ui.account

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.data.IdentityModel
import org.stacks.app.domain.GetUserAuthState
import org.stacks.app.domain.GetUserAuthState.UserAuthState.Authenticated
import org.stacks.app.domain.GetUserAuthState.UserAuthState.Unauthenticated
import org.stacks.app.ui.BaseViewModel

class AccountViewModel
@ViewModelInject constructor(
    getUserAuthState: GetUserAuthState
) : BaseViewModel() {

    // Outputs
    private val identities = BroadcastChannel<List<IdentityModel>>(1)
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
    }

    // Outputs
    fun identities(): Flow<List<IdentityModel>> = identities.asFlow()
    fun errors(): Flow<Unit> = errors.asFlow()

}
