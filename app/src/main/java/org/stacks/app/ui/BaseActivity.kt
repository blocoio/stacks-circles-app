package org.stacks.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import org.stacks.app.StacksApp
import org.stacks.app.ui.shared.ImageLoader


abstract class BaseActivity : AppCompatActivity() {

    private val app get() = applicationContext as StacksApp

    protected val imageLoader by lazy { ImageLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }
}
