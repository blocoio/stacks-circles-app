package org.stacks.app.data

import com.tfcporciuncula.flow.FlowSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.stacks.app.data.interfaces.IdentityRepository
import javax.inject.Inject
import javax.inject.Provider

class EncryptedPreferencesIdentityRepository
@Inject constructor(
    private val preferencesProvider: Provider<FlowSharedPreferences>
) : IdentityRepository {

    private val flowSharedPreferences: FlowSharedPreferences by lazy { preferencesProvider.get() }
    private val preferences by lazy { flowSharedPreferences.getString(IDENTITY) }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        preferences.setAndCommit("") //DELETE does not trigger observe
    }

    override suspend fun set(model: IdentityModel) = withContext(Dispatchers.IO) {
        preferences.setAndCommit(model.json.toString())
    }

    override suspend fun observe(): Flow<IdentityModel?> = withContext(Dispatchers.IO) {
        preferences
            .asFlow()
            .map {
                if (it.isEmpty()) return@map null
                IdentityModel(JSONObject(it))
            }
    }

    companion object {
        const val IDENTITY = "identityModel"
    }

}