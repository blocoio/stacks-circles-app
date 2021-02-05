package io.bloco.circles.ui.shared

import android.content.ClipData
import android.content.ClipboardManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ClipboardUtils
@Inject constructor(
    private val clipboard: ClipboardManager
) {

    suspend fun setText(text: String, label: String = UUID.randomUUID().toString()) =
        withContext(Dispatchers.IO) {
            clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        }

}
