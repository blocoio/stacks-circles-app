package io.bloco.circles.ui.shared

import android.app.Activity
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class MessageLoader
@Inject constructor(
    private val activity: Activity
) {

    private val rootView get() = activity.findViewById<View>(android.R.id.content)

    fun show(@StringRes resString: Int) =
        Snackbar.make(rootView, activity.getString(resString), Snackbar.LENGTH_LONG)
            .show()


    fun show(string: String) =
        Snackbar.make(rootView, string, Snackbar.LENGTH_LONG)
        .show()

}
