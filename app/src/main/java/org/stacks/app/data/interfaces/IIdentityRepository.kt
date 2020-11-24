package org.stacks.app.data.interfaces

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import org.stacks.app.data.IdentityModel
import org.stacks.app.data.IdentityRepository

interface IIdentityRepository {
    suspend fun clear(): Boolean
    suspend fun set(model: IdentityModel): Boolean
    suspend fun observe(): Flow<IdentityModel?>
}

@Module
@InstallIn(ApplicationComponent::class)
abstract class AnalyticsModule {

    @Binds
    abstract fun bindIdentityRepository(
        repository: IdentityRepository
    ): IIdentityRepository
}
