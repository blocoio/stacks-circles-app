package io.bloco.circles.domain

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import me.uport.sdk.core.toBase64UrlSafe
import me.uport.sdk.jwt.JWTSignerAlgorithm
import me.uport.sdk.jwt.model.JwtHeader
import me.uport.sdk.signer.KPSigner
import org.blockstack.android.sdk.Blockstack
import org.blockstack.android.sdk.toBtcAddress
import org.blockstack.android.sdk.toHexPublicKey64
import org.json.JSONArray
import org.kethereum.extensions.toHexString
import org.kethereum.model.ECKeyPair
import io.bloco.circles.data.ProfileModel
import io.bloco.circles.data.network.services.HubService
import io.bloco.circles.shared.nextYear
import io.bloco.circles.shared.toZuluTime
import java.util.*
import javax.inject.Inject

class UploadProfile
@Inject constructor(
    private val hubService: HubService,
    private val blockstack: Blockstack,
    private val generateAuthToken: GenerateAuthToken,
    private val gson: Gson
) {

    suspend fun upload(profile: ProfileModel, keys: ECKeyPair): Unit =
        withContext(IO) {
            val payload = profilePayload(profile, keys.toHexPublicKey64())
            val token = payloadToToken(payload, keys.privateKey.key.toHexString())
            val wrappedToken = blockstack.wrapProfileToken(token)
            val authToken = generateAuthToken.generate(keys)

            hubService.updateProfile(
                keys.toBtcAddress(),
                "bearer $authToken",
                JSONArray().put(wrappedToken.json).toString()
            )
        }

    private fun profilePayload(profile: ProfileModel, publicKey: String): Map<String, Any> {
        val map = mutableMapOf(
            "jti" to UUID.randomUUID().toString(),
            "iat" to Date().toZuluTime(),
            "exp" to nextYear().toZuluTime(),
            "subject" to mapOf("publicKey" to publicKey),
            "issuer" to mapOf("publicKey" to publicKey),
            "claim" to mapOf(
                "@type" to profile.type,
                "@context" to profile.context
            )
        )

        if (profile.apps.isNotEmpty()) {
            map["apps"] = profile.apps
        }

        if (profile.appsMeta.isNotEmpty()) {
            map["appsMeta"] = profile.appsMeta
        }

        return map
    }

    private suspend fun payloadToToken(payload: Map<String, Any>, privateKey: String): String {
        val header = JwtHeader(alg = JwtHeader.ES256K)
        val serializedPayload = gson.toJson(payload)
        val signingInput =
            listOf(header.toJson(), serializedPayload).joinToString(".") { it.toBase64UrlSafe() }

        val signature = JWTSignerAlgorithm(header).sign(signingInput, KPSigner(privateKey))
        return listOf(signingInput, signature).joinToString(".")
    }

}
