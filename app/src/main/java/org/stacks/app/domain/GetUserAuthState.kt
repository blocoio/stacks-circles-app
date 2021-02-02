package org.stacks.app.domain

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.interfaces.IdentityRepository
import javax.inject.Inject

class GetUserAuthState
@Inject constructor(
    private val identityRepository: IdentityRepository
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
            .catch { emit(UserAuthState.Unauthenticated) }

    // State
    sealed class UserAuthState {
        data class Authenticated(val identities: List<IdentityModel>) : UserAuthState() {
            inline val mainIdentity
                get() = identities.first()
        }

        object Unauthenticated : UserAuthState()
    }
}