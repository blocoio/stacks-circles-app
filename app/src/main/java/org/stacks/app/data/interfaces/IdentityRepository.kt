package org.stacks.app.data.interfaces

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.EncryptedPreferencesIdentityRepository

interface IdentityRepository {
    suspend fun clear(): Boolean
    suspend fun set(model: IdentityModel): Boolean
    suspend fun observe(): Flow<IdentityModel?>
}

@Module
@InstallIn(ApplicationComponent::class)
abstract class IdentityBinding {

    @Binds
    abstract fun bindEncryptedPreferencesIdentityRepository(
        repositoryEncryptedPreferences: EncryptedPreferencesIdentityRepository
    ): IdentityRepository
}
