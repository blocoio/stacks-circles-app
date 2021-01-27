package org.stacks.app.domain

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import org.blockstack.android.sdk.model.Hub
import org.json.JSONObject
import org.kethereum.extensions.toHexString
import org.kethereum.model.ECKeyPair
import org.stacks.app.R
import org.stacks.app.data.network.services.HubService
import javax.inject.Inject

class GenerateAuthToken
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val hubService: HubService,
    private val hub: Hub,
    private val gson: Gson,
) {

    suspend fun generate(keys: ECKeyPair): String {
        val hubInfo = hubService.hubInfo()

        return hub.makeV1GaiaAuthToken(
            JSONObject(gson.toJson(hubInfo)),
            keys.privateKey.key.toHexString(),
            context.getString(R.string.hub_endpoint),
            null,
            emptyArray()
        )
    }
}