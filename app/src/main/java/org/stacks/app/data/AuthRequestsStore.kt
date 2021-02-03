package org.stacks.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class AuthRequestsStore
@Inject constructor() {

    private val storeFlow = MutableStateFlow<AuthRequestModel?>(null)

    suspend fun set(authRequest: AuthRequestModel) = storeFlow.emit(authRequest)
    fun get() = storeFlow.value
    fun isEmpty() = storeFlow.value == null
    fun isNotEmpty() = !isEmpty()

}