package org.stacks.app.ui

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import com.google.gson.Gson
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import me.uport.sdk.core.decodeBase64
import me.uport.sdk.jwt.InvalidJWTException
import me.uport.sdk.jwt.JWTEncodingException
import me.uport.sdk.jwt.JWTUtils
import org.stacks.app.data.AuthRequestModel
import org.stacks.app.data.AuthRequestsStore
import org.stacks.app.domain.GetUserAuthState
import org.stacks.app.domain.GetUserAuthState.UserAuthState.Unauthenticated

class SplashViewModel
@ViewModelInject constructor(
    private val gson: Gson,
    store: AuthRequestsStore,
    userAuthState: GetUserAuthState
) : BaseViewModel() {

    // Inputs
    private val dataReceived = BroadcastChannel<Uri?>(1)

    // Outputs
    private val openHomepage = BroadcastChannel<Unit>(1)
    private val openConnect = BroadcastChannel<Unit>(1)
    private val openIdentities = BroadcastChannel<Unit>(1)
    private val errors = BroadcastChannel<Unit>(1)

    init {

        dataReceived
            .asFlow()
            .onEach { if (it == null) openHomepage.send(Unit) }
            .map { it?.fragment?.substringAfter("authRequest=") }
            .filter { it != null }
            .onEach { authRequestToken ->
                store.set(decodeJWT(authRequestToken!!))

                if (userAuthState.state().first() is Unauthenticated) {
                    openConnect.send(Unit)
                } else {
                    openIdentities.send(Unit)
                }
            }
            .catch { errors.send(Unit) }
            .launchIn(ioScope)

    }

    private fun decodeJWT(token: String): AuthRequestModel {
        //Split token by . from jwtUtils
        val (encodedHeader, encodedPayload, _) = JWTUtils.splitToken(token)
        if (encodedHeader.isEmpty())
            throw InvalidJWTException("Header cannot be empty")
        else if (encodedPayload.isEmpty())
            throw InvalidJWTException("Payload cannot be empty")
        //Decode the pieces
        val payloadString = String(encodedPayload.decodeBase64())

        try {
            return gson.fromJson(payloadString, AuthRequestModel::class.java)
        } catch (ex: Throwable) {
            throw JWTEncodingException("cannot parse the JWT($token)", ex)
        }
    }

    // Inputs
    suspend fun dataReceived(data: Uri?) = dataReceived.send(data)

    // Outputs
    fun openHomepage() = openHomepage.asFlow()
    fun openConnect() = openConnect.asFlow()
    fun openIdentities() = openIdentities.asFlow()
    fun errors() = errors.asFlow()

}
