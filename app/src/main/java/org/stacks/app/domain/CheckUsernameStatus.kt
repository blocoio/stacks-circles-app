package org.stacks.app.domain

import org.stacks.app.data.network.models.RegistrarName.RegistrarNameStatus
import org.stacks.app.data.network.services.RegistrarService
import org.stacks.app.domain.CheckUsernameStatus.UsernameStatus.*
import retrofit2.HttpException
import javax.inject.Inject

class CheckUsernameStatus
@Inject constructor(
    private val registrarService: RegistrarService
) {

    //TODO: this needs test coverage
    // we we response code and not status itself
    suspend fun isAvailable(username: String): UsernameStatus = try {
        when(registrarService.lookupName("$username.id.blockstack").status) {
            RegistrarNameStatus.Available -> Available
            RegistrarNameStatus.InvalidName -> Invalid
            RegistrarNameStatus.SubmittedSubDomain -> Unavailable
            else -> Error
        }
    } catch (e: HttpException) {
        when(e.code()) {
            400 -> Invalid
            404 -> Available
            else -> Error
        }
    } catch (e: Throwable) {
        Error
    }

    enum class UsernameStatus {
        Available, Unavailable, Invalid, Error
    }

}