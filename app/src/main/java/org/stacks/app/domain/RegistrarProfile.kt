package org.stacks.app.domain

import org.stacks.app.data.network.services.RegistrarService
import javax.inject.Inject

class RegistrarProfile
@Inject constructor(
    registrarService: RegistrarService
){
    fun register(username: String, address: String) {
        TODO("Not yet implemented")
    }

}
