package org.stacks.app.ui.account

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.interfaces.IdentityRepository
import org.stacks.app.data.interfaces.SecretKeyRepository
import org.stacks.app.ui.BaseViewModel

class AccountViewModel
@ViewModelInject constructor(
    identityRepository: IdentityRepository,
    secretKeyRepository: SecretKeyRepository
) : BaseViewModel() {

    // Inputs
    private val logout = BroadcastChannel<Unit>(1)

    // Outputs
    private val identities = BroadcastChannel<List<IdentityModel>>(1)

    init {
        identityRepository
            .observe()
            .onEach {
                identities.send(it)
            }
            .launchIn(ioScope)

        logout
            .asFlow()
            .onEach {
                if (identityRepository.clear() && secretKeyRepository.clear()) {
                    identities.send(emptyList())
                }
            }
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun logoutPressed() = logout.send(Unit)

    // Outputs
    fun identities(): Flow<List<IdentityModel>> = identities.asFlow()

}
