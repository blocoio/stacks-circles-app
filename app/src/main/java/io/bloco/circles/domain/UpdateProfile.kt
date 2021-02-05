package io.bloco.circles.domain

import com.google.gson.Gson
import org.blockstack.android.sdk.Blockstack
import org.blockstack.android.sdk.model.BlockstackAccount
import org.blockstack.android.sdk.toHexPublicKey64
import org.json.JSONArray
import org.kethereum.crypto.toECKeyPair
import org.kethereum.model.PrivateKey
import org.komputing.khex.model.HexString
import io.bloco.circles.data.ProfileModel
import io.bloco.circles.data.ProfileModel.AppMetaData
import io.bloco.circles.data.network.services.GaiaService
import javax.inject.Inject

class UpdateProfile
@Inject constructor(
    private val gaiaService: GaiaService,
    private val upProfile: UploadProfile,
    private val blockstack: Blockstack,
    private val gson: Gson
) {

    suspend fun update(
        domainName: String,
        account: BlockstackAccount,
    ) {
        val jwtProfiles = JSONArray(gaiaService.profile(account.ownerAddress).string())

        val decodedToken =
            blockstack.decodeToken(jwtProfiles.getJSONObject(0).getString("token")).second

        val profile = gson.fromJson(decodedToken.toString(), ProfileModel::class.java)

        val apps = profile.apps.toMutableMap()
        val appsMeta = profile.appsMeta.toMutableMap()

        val appNode = account.getAppsNode().getAppNode(domainName)
        val appAddress = appNode.toBtcAddress()
        val appPublicKey =
            PrivateKey(HexString(appNode.getPrivateKeyHex())).toECKeyPair().toHexPublicKey64()

        val appHub = "https:\\/\\/gaia.blockstack.org\\/hub\\/$appAddress\\/"

        if (!apps.containsKey(domainName)) {
            apps[domainName] = appHub
        }

        if (!appsMeta.containsKey(domainName)) {
            appsMeta[domainName] = AppMetaData(
                appPublicKey,
                appHub
            )
        }

        upProfile.upload(profile, account.keys.keyPair)
    }

}
