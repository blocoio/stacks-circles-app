package org.stacks.app.domain

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.stacks.app.data.AppDetails
import org.stacks.app.data.AppManifestModel
import org.stacks.app.data.AuthRequestModel
import javax.inject.Inject

class GetAppDetails
@Inject constructor(
    private val gson: Gson
) {

    fun get(authRequest: AuthRequestModel): AppDetails? {
        if (authRequest.appDetails != null) return authRequest.appDetails

        val request = Request.Builder().url(authRequest.manifestUri).build()
        val response = OkHttpClient().newCall(request).execute()

        return if (response.isSuccessful && response.body != null) {
            val appManifest = gson.fromJson(response.body!!.string(), AppManifestModel::class.java)
            AppDetails(
                appManifest.icons.first().src,
                appManifest.name
            )
        } else {
            null
        }
    }

}
