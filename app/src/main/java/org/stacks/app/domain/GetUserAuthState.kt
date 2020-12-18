package org.stacks.app.domain

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import org.stacks.app.data.EncryptedPreferencesIdentityRepository
import org.stacks.app.data.IdentityModel
import javax.inject.Inject

class GetUserAuthState
@Inject constructor(
    private val identityRepository: EncryptedPreferencesIdentityRepository
) {

    // Outputs
    fun state() =
        identityRepository
            .observe()
            .take(1)
            .map {
                if (it.isNotEmpty()) {
                    UserAuthState.Authenticated(it)
                } else {
                    UserAuthState.Unauthenticated
                }
            }

    // State
    sealed class UserAuthState {
        data class Authenticated(val identities: List<IdentityModel>) : UserAuthState() {
            inline val mainIdentity
                get() = identities.first()
        }

        object Unauthenticated : UserAuthState()
    }
}