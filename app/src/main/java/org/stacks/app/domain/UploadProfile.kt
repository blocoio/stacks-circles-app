package org.stacks.app.domain

import org.kethereum.model.ECKeyPair
import org.stacks.app.data.ProfileModel
import org.stacks.app.data.network.services.GaiaService
import org.stacks.app.data.network.services.HubService
import javax.inject.Inject

class UploadProfile
@Inject constructor(
    hubService: HubService,
    gaiaService: GaiaService,
){

    fun upload(profile: ProfileModel, keys: ECKeyPair) {
        //1st post in hub
        TODO("Not yet implemented")
    }
}