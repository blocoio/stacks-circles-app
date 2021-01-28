package org.stacks.app.ui.secret

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.stacks.app.data.interfaces.SecretKeyRepository
import org.stacks.app.domain.GenerateSecretKey
import org.stacks.app.ui.BaseViewModel
import org.stacks.app.ui.shared.ClipboardUtils

class SecretKeyViewModel
@ViewModelInject constructor(
    secretKeyRepository: SecretKeyRepository,
    generateSecretKey: GenerateSecretKey,
    clipboardUtils: ClipboardUtils
) : BaseViewModel() {

    // Inputs
    private val copyPressed = BroadcastChannel<Unit>(1)
    private val signUp = BroadcastChannel<Unit>(1)

    // Outputs
    private val secretKey = MutableStateFlow("")

    init {
        secretKeyRepository
            .observe()
            .onEach {
                secretKey.emit(it)
            }
            .launchIn(ioScope)

        copyPressed
            .asFlow()
            .onEach {
                clipboardUtils.setText(secretKey.value)
            }
            .launchIn(viewModelScope)

        signUp
            .asFlow()
            .map {
                generateSecretKey.generate()
            }
            .onEach {
                secretKey.emit(it)
            }
            .launchIn(ioScope)
    }

    // Inputs
    suspend fun copyPressed() = copyPressed.send(Unit)
    suspend fun signUp() = signUp.send(Unit)

    // Output
    fun secretKey() = secretKey.asStateFlow()

}
