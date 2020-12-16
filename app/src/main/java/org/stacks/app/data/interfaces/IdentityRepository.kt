package org.stacks.app.data.interfaces

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import org.stacks.app.data.EncryptedPreferencesIdentityRepository
import org.stacks.app.data.IdentityModel

interface IdentityRepository {
    suspend fun clear(): Boolean
    suspend fun set(models: List<IdentityModel>): Boolean
    fun observe(): Flow<List<IdentityModel>>
}

@Module
@InstallIn(ApplicationComponent::class)
abstract class IdentityBinding {

    @Binds
    abstract fun bindEncryptedPreferencesIdentityRepository(
        repositoryEncryptedPreferences: EncryptedPreferencesIdentityRepository
    ): IdentityRepository
}
