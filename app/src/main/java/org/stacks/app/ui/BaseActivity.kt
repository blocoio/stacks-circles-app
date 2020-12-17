package org.stacks.app.ui

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import kotlinx.android.synthetic.main.partial_tool_bar.*
import org.stacks.app.R
import org.stacks.app.StacksApp
import org.stacks.app.ui.shared.ImageLoader


abstract class BaseActivity : AppCompatActivity() {

    private val app get() = applicationContext as StacksApp

    protected val imageLoader by lazy { ImageLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }


    fun setNavigation(
        @DrawableRes icon: Int = R.drawable.ic_arrow_back_18,
        clickListener: (() -> Unit) = { onBackPressed() }
    ) {
        toolbarTitle?.text = title
        toolbar?.setNavigationIcon(icon)
        toolbar?.setNavigationOnClickListener { clickListener.invoke() }
        toolbar?.setNavigationContentDescription(R.string.back_content_description)
    }
}
