package org.stacks.app.ui.secret

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import org.stacks.app.data.interfaces.SecretKeyRepository
import org.stacks.app.ui.BaseViewModel
import org.stacks.app.ui.shared.ClipboardUtils

class SecretKeyViewModel
@ViewModelInject constructor(
    secretKeyRepository: SecretKeyRepository,
    clipboardUtils: ClipboardUtils
) : BaseViewModel() {

    // Inputs
    private val copyPressed = BroadcastChannel<Unit>(1)

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
    }

    // Inputs
    suspend fun copyPressed() = copyPressed.send(Unit)

    // Output
    fun secretKey() = secretKey.asStateFlow()

}
