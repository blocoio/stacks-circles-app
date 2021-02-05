package io.bloco.circles.data.interfaces

import kotlinx.coroutines.flow.Flow
import io.bloco.circles.data.IdentityModel

interface IdentityRepository {
    suspend fun clear(): Boolean
    suspend fun set(models: List<IdentityModel>): Boolean
    fun observe(): Flow<List<IdentityModel>>
}
