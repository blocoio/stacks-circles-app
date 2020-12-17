package org.stacks.app.ui.secret.bottomsheet

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.stacks.app.data.interfaces.SecretKeyRepository
import org.stacks.app.ui.BaseViewModel
import org.stacks.app.ui.shared.ClipboardUtils

class ShareKeyBottomSheetViewModel
@ViewModelInject constructor(
    secretKeyRepository: SecretKeyRepository,
    clipboardUtils: ClipboardUtils
) : BaseViewModel() {

    // Inputs
    private val copyPressed = BroadcastChannel<Unit>(1)

    init {

        ioScope.launch {
            secretKey = secretKeyRepository
                .observe()
                .first()
        }

        copyPressed
            .asFlow()
            .onEach {
                clipboardUtils.setText(secretKey)
            }
            .launchIn(viewModelScope)

    }

    // Inputs
    suspend fun copyPressed() = copyPressed.send(Unit)

    // Outputs
    var secretKey: String = ""

}
