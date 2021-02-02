package org.stacks.app.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import org.stacks.app.ui.BaseActivity
import org.stacks.app.ui.homepage.HomepageActivity
import reactivecircus.flowbinding.android.view.clicks

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        discover
            .clicks()
            .onEach {
                //TODO: see if there's an auth request, if there is set result
                // also only do this on SIGNUP

                startActivity(HomepageActivity.getIntent(this))
            }
            .launchIn(lifecycleScope)
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, WelcomeActivity::class.java)
    }

}