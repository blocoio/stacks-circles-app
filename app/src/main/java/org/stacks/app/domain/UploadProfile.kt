package org.stacks.app.domain

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import me.uport.sdk.core.toBase64UrlSafe
import me.uport.sdk.jwt.JWTSignerAlgorithm
import me.uport.sdk.jwt.model.ArbitraryMapSerializer
import me.uport.sdk.jwt.model.JwtHeader
import me.uport.sdk.signer.KPSigner
import okhttp3.ResponseBody
import org.blockstack.android.sdk.Blockstack
import org.blockstack.android.sdk.model.Hub
import org.blockstack.android.sdk.toBtcAddress
import org.json.JSONArray
import org.json.JSONObject
import org.kethereum.extensions.toHexString
import org.kethereum.model.ECKeyPair
import org.stacks.app.data.ProfileModel
import org.stacks.app.data.network.services.HubService
import org.stacks.app.shared.nextYear
import org.stacks.app.shared.toZuluTime
import java.util.*
import javax.inject.Inject

class UploadProfile
@Inject constructor(
    private val hubService: HubService,
    private val blockstack: Blockstack,
    private val hub: Hub,
    private val gson: Gson
) {

    suspend fun upload(profile: ProfileModel, keys: ECKeyPair): ResponseBody =
        withContext(IO) {
            val payload = profilePayload(profile, keys.publicKey.key.toHexString())
            val token = payloadToToken(payload, keys.privateKey.key.toHexString())

            val wrappedToken = blockstack.wrapProfileToken(token)

            val hubInfo = hubService.hubInfo()
            val authToken = hub.makeV1GaiaAuthToken(
                JSONObject(gson.toJson(hubInfo)),
                keys.privateKey.key.toHexString(),
                hubInfo.readUrlPrefix,
                null,
                emptyArray()
            )

            return@withContext hubService.updateProfile(
                keys.toBtcAddress(),
                "bearer $authToken",
                JSONArray().put(wrappedToken.json).toString()
            )
        }

    private fun profilePayload(profile: ProfileModel, publicKey: String) = mapOf(
        "jti" to UUID.randomUUID().toString(),
        "iat" to Date().toZuluTime(),
        "exp" to nextYear().toZuluTime(),
        "subject" to mapOf("public Key" to publicKey),
        "issuer" to mapOf("publicKey" to publicKey),
        "claim" to mapOf(
            "@type" to profile.type,
            "@context" to profile.context
        )
    )

    private suspend fun payloadToToken(payload: Map<String, Any>, privateKey: String): String {
        val header = JwtHeader(alg = JwtHeader.ES256K)
        val serializedPayload = Json(JsonConfiguration.Stable)
            .stringify(ArbitraryMapSerializer, payload)
        val signingInput =
            listOf(header.toJson(), serializedPayload).joinToString(".") { it.toBase64UrlSafe() }

        val signature = JWTSignerAlgorithm(header).sign(signingInput, KPSigner(privateKey))
        return listOf(signingInput, signature).joinToString(".")
    }

}