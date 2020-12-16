package org.stacks.app.data.interfaces

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import org.stacks.app.data.EncryptedPreferencesSecretKeyRepository

interface SecretKeyRepository {
    suspend fun clear(): Boolean
    suspend fun set(secretKey: String): Boolean
    fun observe(): Flow<String>
}

@Module
@InstallIn(ApplicationComponent::class)
abstract class SecretKeyBinding {

    @Binds
    abstract fun bindEncryptedPreferencesIdentityRepository(
        encryptedPreferencesSecretKeyRepository: EncryptedPreferencesSecretKeyRepository
    ): SecretKeyRepository
}