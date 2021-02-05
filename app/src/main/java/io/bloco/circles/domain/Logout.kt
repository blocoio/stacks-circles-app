package io.bloco.circles.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import io.bloco.circles.data.interfaces.IdentityRepository
import io.bloco.circles.data.interfaces.SecretKeyRepository
import io.bloco.circles.shared.toResult
import javax.inject.Inject

class Logout
@Inject constructor(
    private val identitiesRepo: IdentityRepository,
    private val secretKeyRepo: SecretKeyRepository
){
    fun logout(): Flow<Result<Boolean>> =
        flow { emit(identitiesRepo.clear()) }
            .map { secretKeyRepo.clear() }
            .toResult()
}
