package io.bloco.circles.ui.secret.bottomsheet

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import io.bloco.circles.data.interfaces.SecretKeyRepository
import io.bloco.circles.ui.BaseViewModel
import io.bloco.circles.ui.shared.ClipboardUtils

class ShareKeyBottomSheetViewModel
@ViewModelInject constructor(
    secretKeyRepository: SecretKeyRepository,
    clipboardUtils: ClipboardUtils
) : BaseViewModel() {

    // Inputs
    private val copyPressed = BroadcastChannel<Unit>(1)

    // Outputs
    private val secretKey = MutableStateFlow("")
    private val copiedSuccessful = BroadcastChannel<Unit>(1)

    init {

        secretKeyRepository
            .observe()
            .take(1)
            .onEach { secretKey.emit(it) }
            .launchIn(ioScope)

        copyPressed
            .asFlow()
            .onEach {
                clipboardUtils.setText(secretKey.value)
                copiedSuccessful.send(Unit)
            }
            .launchIn(viewModelScope)

    }

    // Inputs
    suspend fun copyPressed() =
        copyPressed.send(Unit)

    // Outputs
    fun secretKey() = secretKey.asStateFlow()
    fun copiedSuccessful() = copiedSuccessful.asFlow()

}
