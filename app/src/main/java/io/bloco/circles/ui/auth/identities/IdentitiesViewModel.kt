package io.bloco.circles.ui.auth.identities

import dagger.hilt.android.lifecycle.HiltViewModel
import io.bloco.circles.data.AuthRequestModel.AppDetails
import io.bloco.circles.data.AuthRequestsStore
import io.bloco.circles.data.AuthResponseModel
import io.bloco.circles.data.IdentityModel
import io.bloco.circles.data.interfaces.IdentityRepository
import io.bloco.circles.domain.GenerateAuthResponse
import io.bloco.circles.domain.GetAppDetails
import io.bloco.circles.domain.NewIdentity
import io.bloco.circles.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class IdentitiesViewModel
@Inject constructor(
    private val authRequestsStore: AuthRequestsStore,
    generateAuthResponse: GenerateAuthResponse,
    identityRepository: IdentityRepository,
    getAppDetails: GetAppDetails,
    newIdentity: NewIdentity,
) : BaseViewModel() {

    // Inputs
    private val identitySelected = BroadcastChannel<IdentityModel>(1)
    private val createNewIdentity = BroadcastChannel<Unit>(1)

    // Outputs
    private val chooseNewIdentityUsername = BroadcastChannel<Unit>(1)
    private val sendAuthResponse = BroadcastChannel<AuthResponseModel>(1)
    private val identities = BroadcastChannel<List<IdentityModel>>(1)
    private val appDetails = MutableStateFlow<AppDetails?>( null)
    private val loading = BroadcastChannel<Boolean>(1)
    private val errors = BroadcastChannel<Unit>(1)


    init {
        identityRepository
            .observe()
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

        createNewIdentity
            .asFlow()
            .onEach {
                authRequestsStore.get()?.let { request ->
                    if(request.registerSubdomain) {
                        chooseNewIdentityUsername.send(Unit)
                    } else {
                        loading.send(true)
                        newIdentity.create()
                        loading.send(false)
                    }
                }
            }
            .launchIn(ioScope)

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
            .catch { e ->
                loading.send(false)
                errors.send(Unit)
                emitAll(flow { IdentityModel(JSONObject("")) })
                Timber.e(e)
            }
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun identitySelected(identity: IdentityModel) = identitySelected.send(identity)
    suspend fun createNewIdentity() = createNewIdentity.send(Unit)

    // Outputs
    fun chooseNewIdentityUsername(): Flow<Unit> = chooseNewIdentityUsername.asFlow()
    fun sendAuthResponse(): Flow<AuthResponseModel> = sendAuthResponse.asFlow()
    fun identities(): Flow<List<IdentityModel>> = identities.asFlow()
    fun appDetails(): Flow<AppDetails?> = appDetails.asStateFlow()
    fun appDomain() = authRequestsStore.get()?.domainName ?: ""
    fun loading() = loading.asFlow()
    fun errors() = errors.asFlow()


}
