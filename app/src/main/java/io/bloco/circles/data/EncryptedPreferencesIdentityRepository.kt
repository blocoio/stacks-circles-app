package io.bloco.circles.data

import com.tfcporciuncula.flow.FlowSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import io.bloco.circles.data.interfaces.IdentityRepository
import io.bloco.circles.shared.IdentitiesParsingFailed
import io.bloco.circles.shared.PolluteString
import io.bloco.circles.shared.forEach
import io.bloco.circles.shared.toFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class EncryptedPreferencesIdentityRepository
@Inject constructor(
    private val preferencesProvider: Provider<FlowSharedPreferences>
) : IdentityRepository {

    private val flowSharedPreferences: FlowSharedPreferences by lazy { preferencesProvider.get() }
    private val preferences by lazy { flowSharedPreferences.getString(IDENTITY) }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        preferences.setAndCommit(PolluteString.pollute(preferences.get().length))
    }

    override suspend fun set(models: List<IdentityModel>) = withContext(Dispatchers.IO) {
        preferences.setAndCommit(JSONArray(models.map { it.json }).toString())
    }

    override fun observe(): Flow<List<IdentityModel>> =
        { preferences }
            .toFlow()
            .map {
                toListIdentities(it)
            }

    /**
     * Converts the given string to a list of [IdentityModel]'s
     *
     * @param jsonArrayString - string that contains the JSON array with the Identities
     *
     * @throws IdentitiesParsingFailed if it fails to convert the identities
     */
    private fun toListIdentities(jsonArrayString: String): List<IdentityModel> {
        if (jsonArrayString.isEmpty() || PolluteString.isPolluted(jsonArrayString)) return emptyList()

        val listOfIdentities = mutableListOf<IdentityModel>()

        try {
            val identities = JSONArray(jsonArrayString)

            identities.forEach<JSONObject> {
                listOfIdentities.add(IdentityModel(it))
            }

        } catch (e: Throwable) {
            Timber.w(e)
            throw IdentitiesParsingFailed(e)
        }

        return listOfIdentities
    }

    companion object {
        const val IDENTITY = "identityModel"
    }

}
