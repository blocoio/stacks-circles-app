package org.stacks.app.ui.auth.identities

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.stacks.app.data.AuthRequestModel.AppDetails
import org.stacks.app.data.AuthRequestsStore
import org.stacks.app.data.AuthResponseModel
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
    private val sendAuthResponse = BroadcastChannel<AuthResponseModel>(1)
    private val identities = BroadcastChannel<List<IdentityModel>>(1)
    private val appDetails = MutableStateFlow<AppDetails?>( null)
    private val loading = BroadcastChannel<Boolean>(1)
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
                    appDetails.emit(it)
                }
            }
        }

        identitySelected
            .asFlow()
            .onEach {
                loading.send(true)
                val authRequest = authRequestsStore.get()!!

                sendAuthResponse.send(
                    AuthResponseModel(
                        appDetails.value?.name ?: "",
                        authRequest.redirectUri,
                        generateAuthResponse.generate(it)
                    )
                )
            }
            .catch {
                loading.send(false)
                errors.send(Unit)
                emitAll(flow { IdentityModel(JSONObject("")) })
            }
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun identitySelected(identity: IdentityModel) = identitySelected.send(identity)

    // Outputs
    fun sendAuthResponse(): Flow<AuthResponseModel> = sendAuthResponse.asFlow()
    fun identities(): Flow<List<IdentityModel>> = identities.asFlow()
    fun appDetails(): Flow<AppDetails?> = appDetails.asStateFlow()
    fun appDomain() = authRequestsStore.get()?.domainName ?: ""
    fun loading() = loading.asFlow()
    fun errors() = errors.asFlow()


}
