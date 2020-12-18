package org.stacks.app.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.stacks.app.data.interfaces.IdentityRepository
import org.stacks.app.data.interfaces.SecretKeyRepository
import org.stacks.app.shared.toResult
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