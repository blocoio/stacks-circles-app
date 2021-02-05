package io.bloco.circles.data

import com.tfcporciuncula.flow.FlowSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withContext
import io.bloco.circles.data.interfaces.SecretKeyRepository
import io.bloco.circles.shared.PolluteString
import io.bloco.circles.shared.toFlow
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class EncryptedPreferencesSecretKeyRepository
@Inject constructor(
    private val preferencesProvider: Provider<FlowSharedPreferences>
) : SecretKeyRepository {

    private val flowSharedPreferences: FlowSharedPreferences by lazy { preferencesProvider.get() }
    private val preferences by lazy { flowSharedPreferences.getString(SECRET_KEY) }

    override suspend fun clear(): Boolean = withContext(Dispatchers.IO) {
        preferences.setAndCommit(PolluteString.pollute(preferences.get().length))
    }

    override suspend fun set(secretKey: String): Boolean = withContext(Dispatchers.IO) {
        preferences.setAndCommit(secretKey)
    }

    override fun observe(): Flow<String> =
        { preferences }
            .toFlow()
            .filter { !PolluteString.isPolluted(it) }

    companion object {
        const val SECRET_KEY = "secret_key"
    }
}
