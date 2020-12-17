package org.stacks.app.ui.shared

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class ClipboardUtils
@Inject constructor(
    @ApplicationContext context: Context
) {
    private val clipboard =
        ContextCompat.getSystemService(context, ClipboardManager::class.java) as ClipboardManager


    fun setText(text: String, label: String = UUID.randomUUID().toString()) {
        ClipData.newPlainText(label, text)
    }

}