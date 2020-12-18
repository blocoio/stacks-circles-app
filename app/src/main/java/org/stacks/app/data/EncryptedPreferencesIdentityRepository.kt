package org.stacks.app.data

import com.tfcporciuncula.flow.FlowSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.stacks.app.data.interfaces.IdentityRepository
import org.stacks.app.shared.IdentitiesParsingFailed
import org.stacks.app.shared.toFlow
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
        preferences.setAndCommit("") //DELETE does not trigger observe
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
        if (jsonArrayString.isEmpty()) return emptyList()

        val listOfIdentities = mutableListOf<IdentityModel>()

        try {
            val identities = JSONArray(jsonArrayString)

            for (i in 0 until identities.length()) {
                listOfIdentities.add(IdentityModel(identities.getJSONObject(i)))
            }
        } catch (e: Exception) {
            Timber.w(e)
            throw IdentitiesParsingFailed(e)
        }

        return listOfIdentities
    }

    companion object {
        const val IDENTITY = "identityModel"
    }


}