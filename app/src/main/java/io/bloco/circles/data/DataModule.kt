package io.bloco.circles.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.bloco.circles.data.interfaces.IdentityRepository
import io.bloco.circles.data.interfaces.SecretKeyRepository

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
