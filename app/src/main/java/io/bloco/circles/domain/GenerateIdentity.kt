package io.bloco.circles.domain

import org.json.JSONObject
import io.bloco.circles.data.IdentityModel
import javax.inject.Inject

class GenerateIdentity
@Inject constructor() {
    
    fun generate(address: String, username: String): IdentityModel {
        val json = JSONObject().apply {
            put(IdentityModel.USERNAME, "$username.id.blockstack")
            put(IdentityModel.ADDRESS, address)
            put(IdentityModel.APP_MODELS, JSONObject())
        }

        return IdentityModel(json)
    }
}
