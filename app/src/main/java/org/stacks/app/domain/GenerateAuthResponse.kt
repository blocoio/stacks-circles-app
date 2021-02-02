package org.stacks.app.domain

import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import org.blockstack.android.sdk.Blockstack
import org.blockstack.android.sdk.Scope
import org.blockstack.android.sdk.model.BlockstackAccount
import org.blockstack.android.sdk.toHexPublicKey64
import org.json.JSONArray
import org.json.JSONObject
import org.kethereum.crypto.toECKeyPair
import org.kethereum.model.PrivateKey
import org.komputing.khex.extensions.toNoPrefixHexString
import org.komputing.khex.model.HexString
import org.stacks.app.data.*
import org.stacks.app.data.IdentityAppModel.Companion.APP_ICON
import org.stacks.app.data.IdentityAppModel.Companion.LAST_LOGIN
import org.stacks.app.data.IdentityAppModel.Companion.NAME
import org.stacks.app.data.IdentityAppModel.Companion.ORIGIN
import org.stacks.app.data.IdentityAppModel.Companion.SCOPES
import org.stacks.app.data.IdentityModel.Companion.APP_MODELS
import org.stacks.app.data.interfaces.IdentityRepository
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class GenerateAuthResponse
@Inject constructor(
    private val identityRepository: IdentityRepository,
    private val authRequestsStore: AuthRequestsStore,
    private val identityKeys: IdentityKeys,
    private val blockstack: Blockstack,
    private val upProfile: UploadProfile,
    private val upWallet: UploadWallet,
    private val getAppDetails: GetAppDetails,
    private val gson: Gson
) {

    //TODO: proper Exception
    suspend fun generate(identity: IdentityModel): String {
        val authRequest = authRequestsStore.get() ?: throw Exception()
        val keys = identityKeys.getFrom(identity)
        val salt = ByteArray(16) { 0 }.toNoPrefixHexString()
        val account = BlockstackAccount(identity.completeUsername, keys, salt)

        //TODO: consider extraction to domain class's, complexity is high
        //updateUserWallet(authRequest, identity)
        //updateUserProfile(authRequest, account)

        val payload = JSONObject().apply {
            put("public_keys", JSONArray().put(authRequest.publicKeys.first()))
            put("domain_name", authRequest.domainName)
        }

        val scopes = authRequest.scopes.map {
            Scope(it)
        }.toTypedArray()

        return blockstack.makeAuthResponse(
            payload, account,
            scopes
        )

    }

    private suspend fun updateUserWallet(authRequest: AuthRequestModel, identity: IdentityModel) {
        val identities = identityRepository.observe().first().toMutableList()

        val appDetails = getAppDetails.get(authRequest)
        val appModel = identity.appModels.find { it.name == appDetails?.name ?: "" }


        if (appModel != null) {
            Timber.i(appModel.json.toString() ?: "")
            appModel.json.put(LAST_LOGIN, Date().time)
            Timber.i(appModel.json.toString() ?: "")
        } else {
            val newModel = JSONObject().apply {
                put(NAME, appDetails?.name ?: authRequest.domainName)
                put(APP_ICON, appDetails?.icon ?: "")
                put(LAST_LOGIN, Date().time)
                put(ORIGIN, authRequest.domainName)
                put(SCOPES, authRequest.scopes)
            }

            val appModels = JSONObject()
            val apps = identity.json.getJSONObject(APP_MODELS)
            apps.keys().asSequence().map {
                appModels.put(it, apps.get(it))
            }.toList()

            appModels.put(authRequest.domainName, newModel)

            val identityJson = identity.json

            identityJson.put(APP_MODELS, appModels)


            identities[identities.indexOfFirst { it.username == identity.username }] =
                IdentityModel(
                    identityJson
                )
        }

        Timber.i("New Identities")
        identityRepository.set(identities)
        upWallet.upload(identities)
    }

    private suspend fun updateUserProfile(
        authRequest: AuthRequestModel,
        account: BlockstackAccount,
    ) {
        val profile = blockstack.lookupProfile(account.username!!, null)

        val apps = profile.json.getJSONObject("apps")
        val appsMeta = profile.json.getJSONObject("appsMeta")

        val appKeys = account.getAppsNode().getAppNode(authRequest.domainName)
        val appAddress = appKeys.toBtcAddress()
        val appPKKey =
            PrivateKey(HexString(appKeys.getPrivateKeyHex())).toECKeyPair().toHexPublicKey64()

        val appHub = "https:\\/\\/gaia.blockstack.org\\/hub\\/$appAddress\\/"

        if (!apps.has(authRequest.domainName)) {
            apps.put(authRequest.domainName, appHub)
        }

        if (!appsMeta.has(authRequest.domainName)) {
            appsMeta.put(authRequest.domainName, JSONObject().apply {
                put("storage", appHub)
                put("publicKey", appPKKey)
            })
        }

        val appsMap = mutableMapOf<String, String>()
        apps.keys().forEach {
            appsMap[it] = apps.get(it).toString()
        }

        val appsMetaMap = mutableMapOf<String, AppMetaData>()
        appsMeta.keys().forEach {
            val metaData = gson.fromJson(appsMeta.get(it).toString(), AppMetaData::class.java)
            appsMetaMap[it] = metaData
        }

        val updatedProfile = ProfileModel(
            profile.json.getString("@type"),
            profile.json.getString("@context"),
            appsMap,
            appsMetaMap
        )

        upProfile.upload(updatedProfile, account.keys.keyPair)
    }

}
