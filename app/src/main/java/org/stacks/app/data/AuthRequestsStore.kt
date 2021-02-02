package org.stacks.app.data

import kotlinx.coroutines.flow.MutableStateFlow

class AuthRequestsStore {

    private val storeFlow = MutableStateFlow<AuthRequestModel?>(null)

    suspend fun set(authRequest: AuthRequestModel) = storeFlow.emit(authRequest)
    fun get() = storeFlow.value

}