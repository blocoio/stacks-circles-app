package org.stacks.app.ui.auth.identities

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.stacks.app.data.AppDetails
import org.stacks.app.data.AuthRequestsStore
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.interfaces.IdentityRepository
import org.stacks.app.domain.GenerateAuthResponse
import org.stacks.app.domain.GetAppDetails
import org.stacks.app.ui.BaseViewModel

class IdentitiesViewModel
@ViewModelInject constructor(
    generateAuthResponse: GenerateAuthResponse,
    identityRepository: IdentityRepository,
    private val store: AuthRequestsStore,
    getAppDetails: GetAppDetails
) : BaseViewModel() {

    // Inputs
    private val identitySelected = BroadcastChannel<IdentityModel>(1)

    // Outputs
    private val sendAuthResponse = BroadcastChannel<String>(1)
    private val identities = BroadcastChannel<List<IdentityModel>>(1)
    private val appDetails = BroadcastChannel<AppDetails>(1)
    private val errors = BroadcastChannel<Unit>(1)


    init {
        identityRepository
            .observe()
            .take(1)
            .onEach {
                identities.send(it)
            }
            .launchIn(ioScope)

        GlobalScope.launch(Dispatchers.IO) {
            val authRequest = store.get()

            if (authRequest != null) {
                getAppDetails.get(authRequest)?.also {
                    appDetails.send(it)
                }
            }
        }

        identitySelected
            .asFlow()
            .onEach {
                sendAuthResponse.send(generateAuthResponse.generate(it))
            }
            .catch { errors.send(Unit) }
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun identitySelected(identity: IdentityModel) = identitySelected.send(identity)

    // Outputs
    fun sendAuthResponse(): Flow<String> = sendAuthResponse.asFlow()
    fun identities(): Flow<List<IdentityModel>> = identities.asFlow()
    fun appDetails(): Flow<AppDetails> = appDetails.asFlow()
    fun appDomain() = store.get()?.domainName ?: ""
    fun errors() = errors.asFlow()

}
