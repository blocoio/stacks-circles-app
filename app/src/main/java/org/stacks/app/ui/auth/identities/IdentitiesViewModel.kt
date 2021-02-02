package org.stacks.app.ui.auth.identities

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.stacks.app.data.AppDetails
import org.stacks.app.data.AuthRequestsStore
import org.stacks.app.data.AuthResponse
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.interfaces.IdentityRepository
import org.stacks.app.domain.GenerateAuthResponse
import org.stacks.app.domain.GetAppDetails
import org.stacks.app.ui.BaseViewModel

class IdentitiesViewModel
@ViewModelInject constructor(
    private val authRequestsStore: AuthRequestsStore,
    generateAuthResponse: GenerateAuthResponse,
    identityRepository: IdentityRepository,
    getAppDetails: GetAppDetails
) : BaseViewModel() {

    // Inputs
    private val identitySelected = BroadcastChannel<IdentityModel>(1)

    // Outputs
    private val sendAuthResponse = BroadcastChannel<AuthResponse>(1)
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
            val authRequest = authRequestsStore.get()

            if (authRequest != null) {
                getAppDetails.get(authRequest)?.also {
                    appDetails.send(it)
                }
            }
        }

        identitySelected
            .asFlow()
            .onEach {
                val authRequest = authRequestsStore.get()!!

                sendAuthResponse.send(
                    AuthResponse(
                        getAppDetails.get(authRequest)!!.name,
                        authRequest.redirectUri,
                        generateAuthResponse.generate(it)
                    )
                )
            }
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun identitySelected(identity: IdentityModel) = identitySelected.send(identity)

    // Outputs
    fun sendAuthResponse(): Flow<AuthResponse> = sendAuthResponse.asFlow()
    fun identities(): Flow<List<IdentityModel>> = identities.asFlow()
    fun appDetails(): Flow<AppDetails> = appDetails.asFlow()
    fun appDomain() = authRequestsStore.get()?.domainName ?: ""
    fun errors() = errors.asFlow()

}
