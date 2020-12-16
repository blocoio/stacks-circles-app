package org.stacks.app.ui.account

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.interfaces.IdentityRepository
import org.stacks.app.ui.BaseViewModel

class AccountViewModel
@ViewModelInject constructor(
    identityRepository: IdentityRepository
) : BaseViewModel() {

    // Outputs
    private val identities = BroadcastChannel<List<IdentityModel>>(1)

    init {
        identityRepository
            .observe()
            .onEach {
                identities.send(it)
            }
            .launchIn(ioScope)
    }

    // Outputs
    fun identities(): Flow<List<IdentityModel>> = identities.asFlow()

}
