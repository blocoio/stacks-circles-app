package org.stacks.app.ui.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_connect.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import org.stacks.app.ui.BaseActivity
import org.stacks.app.ui.auth.login.LoginActivity
import org.stacks.app.ui.secret.SecretKeyActivity
import reactivecircus.flowbinding.android.view.clicks

class ConnectActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        newAccount
            .clicks()
            .onEach {
                startActivity(SecretKeyActivity.getIntent(this, true))
            }
            .launchIn(lifecycleScope)

        login
            .clicks()
            .onEach {
                startActivity(LoginActivity.getIntent(this))
            }
            .launchIn(lifecycleScope)

        howItWorks
            .clicks()
            .onEach {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_us_url))))
            }
            .launchIn(lifecycleScope)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, ConnectActivity::class.java)
    }
}