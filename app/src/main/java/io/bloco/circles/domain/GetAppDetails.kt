package io.bloco.circles.domain

import com.google.gson.Gson
import io.bloco.circles.data.AppManifestModel
import io.bloco.circles.data.AuthRequestModel
import io.bloco.circles.data.AuthRequestModel.AppDetails
import io.bloco.circles.data.network.services.GenericService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppDetails
@Inject constructor(
    private val gson: Gson,
    private val genericService: GenericService
) {

    fun get(authRequest: AuthRequestModel): AppDetails? {
        if (authRequest.appDetails != null &&
            authRequest.appDetails.icon.contains("http")
        ) return authRequest.appDetails

        val response = try {
            genericService.get(authRequest.manifestUri).execute()
        } catch (t: Throwable) {
            Response.success(null)
        }

        return if (response.isSuccessful && response.body() != null) {
            val appManifest = gson.fromJson(response.body()!!.string(), AppManifestModel::class.java)
            AppDetails(
                appManifest.icons.first().src,
                appManifest.name
            )
        } else {
            authRequest.appDetails
        }
    }

}
