package io.bloco.circles.domain

import com.google.gson.Gson
import io.bloco.circles.data.network.models.RegistrarRequest
import io.bloco.circles.data.network.models.RegistrarResponse
import io.bloco.circles.data.network.services.RegistrarService
import javax.inject.Inject

class RegistrarProfile
@Inject constructor(
    private val registrarService: RegistrarService,
    private val gson: Gson
) {
    suspend fun register(username: String, btcAddress: String, stxAddress: String): RegistrarResponse =
        registrarService.register(gson.toJson(buildRequest(username, btcAddress, stxAddress)))

    private fun buildRequest(name: String, btcAddress: String, stxAddress: String): RegistrarRequest {
        val completeZoneFile =
            "\$ORIGIN $name.id.stx\n" +
                    "\$TTL 3600" +
                    "\n_http._tcp\tIN\tURI\t10\t1\t" +
                    "\"https://gaia.blockstack.org/hub/$btcAddress/profile.json\"\n\n"
        return RegistrarRequest(name, stxAddress, completeZoneFile)
    }
}
