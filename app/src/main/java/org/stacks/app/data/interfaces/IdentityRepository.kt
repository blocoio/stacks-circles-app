package org.stacks.app.data.interfaces

import kotlinx.coroutines.flow.Flow
import org.stacks.app.data.IdentityModel

interface IdentityRepository {
    suspend fun clear(): Boolean
    suspend fun set(models: List<IdentityModel>): Boolean
    fun observe(): Flow<List<IdentityModel>>
}
