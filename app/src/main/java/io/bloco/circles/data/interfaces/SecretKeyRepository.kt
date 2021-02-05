package io.bloco.circles.data.interfaces

import kotlinx.coroutines.flow.Flow

interface SecretKeyRepository {
    suspend fun clear(): Boolean
    suspend fun set(secretKey: String): Boolean
    fun observe(): Flow<String>
}
