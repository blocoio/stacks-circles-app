package org.stacks.app.domain

import kotlinx.coroutines.flow.Flow
import org.stacks.app.data.network.services.RegistrarService
import javax.inject.Inject

class RegistrarProfile
@Inject constructor(
    registrarService: RegistrarService
){
    fun register(username: String, address: String) : Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

}
