package org.stacks.app.domain

import com.google.gson.Gson
import org.stacks.app.data.network.models.RegistrarRequest
import org.stacks.app.data.network.models.RegistrarResponse
import org.stacks.app.data.network.services.RegistrarService
import javax.inject.Inject

class RegistrarProfile
@Inject constructor(
    private val registrarService: RegistrarService,
    private val gson: Gson
) {
    suspend fun register(username: String, address: String): RegistrarResponse =
        registrarService.register(gson.toJson(buildRequest(username, address)))

    private fun buildRequest(name: String, ownerAddress: String): RegistrarRequest {
        val completeZoneFile =
            "\$ORIGIN $name.id.blockstack\n" +
                    "\$TTL 3600" +
                    "\n_http._tcp\tIN\tURI\t10\t1\t" +
                    "\"https://gaia.blockstack.org/hub/$ownerAddress/profile.json\"\n\n"

        return RegistrarRequest(name, ownerAddress, completeZoneFile)
    }
}
