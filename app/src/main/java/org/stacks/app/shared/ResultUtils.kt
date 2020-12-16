package org.stacks.app.shared

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

fun <T> Flow<T>.toResult(): Flow<Result<T>> =
    map { Result.success(it) }
        .catch { emit(Result.failure(it)) }

fun <T, R> Flow<Result<T>>.mapIfSuccess(mapper: ((T) -> R)): Flow<Result<R>> =
    map { it.map(mapper) }
