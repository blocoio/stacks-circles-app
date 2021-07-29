package io.bloco.circles.domain

import io.bloco.circles.data.IdentityModel
import org.json.JSONObject
import javax.inject.Inject

class GenerateIdentity
@Inject constructor() {

    fun generate(address: String, username: String?): IdentityModel {
        val json = JSONObject().apply {
            username?.let { unwrappedUsername ->
                put(IdentityModel.USERNAME,  "$unwrappedUsername.id.stx")
            }
            put(IdentityModel.ADDRESS, address)
            put(IdentityModel.APP_MODELS, JSONObject())
        }

        return IdentityModel(json)
    }
}
