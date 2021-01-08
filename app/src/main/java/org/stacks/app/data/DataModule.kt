package org.stacks.app.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import org.stacks.app.data.interfaces.IdentityRepository
import org.stacks.app.data.interfaces.SecretKeyRepository

@Module
@InstallIn(ApplicationComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindEncryptedPreferencesIdentityRepository(
        repositoryEncryptedPreferences: EncryptedPreferencesIdentityRepository
    ): IdentityRepository

    @Binds
    abstract fun bindEncryptedPreferencesSecretKeyRepository(
        encryptedPreferencesSecretKeyRepository: EncryptedPreferencesSecretKeyRepository
    ): SecretKeyRepository
}