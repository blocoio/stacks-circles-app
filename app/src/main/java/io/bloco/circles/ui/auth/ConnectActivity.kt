package io.bloco.circles.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_connect.*
import kotlinx.android.synthetic.main.partial_tool_bar.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import io.bloco.circles.R
import io.bloco.circles.ui.BaseActivity
import io.bloco.circles.ui.auth.login.LoginActivity
import io.bloco.circles.ui.secret.SecretKeyActivity
import io.bloco.circles.ui.shared.Insets.addSystemWindowInsetToPadding
import reactivecircus.flowbinding.android.view.clicks

class ConnectActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        toolbar.addSystemWindowInsetToPadding(top = true)
        setNavigation()

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
                startActivity(HowItWorksActivity.getIntent(this))
            }
            .launchIn(lifecycleScope)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, ConnectActivity::class.java)
    }
}
