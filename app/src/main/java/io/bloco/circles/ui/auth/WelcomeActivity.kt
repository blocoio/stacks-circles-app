package io.bloco.circles.ui.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import io.bloco.circles.R
import io.bloco.circles.data.AuthResponseModel
import io.bloco.circles.ui.BaseActivity
import io.bloco.circles.ui.auth.identities.IdentitiesActivity
import io.bloco.circles.ui.homepage.HomepageActivity
import io.bloco.circles.ui.shared.Insets.addSystemWindowInsetToMargin
import io.bloco.circles.ui.shared.Insets.addSystemWindowInsetToPadding
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.partial_tool_bar.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class WelcomeActivity : BaseActivity() {

    private val signUp by lazy {
        intent.getBooleanExtra(SIGNUP, false)
    }

    private val appName by lazy {
        intent?.getStringExtra(APP_NAME)
    }

    private val redirectUrl by lazy {
        intent?.getStringExtra(REDIRECT_URL)
    }

    private val authResponseToken by lazy {
        intent?.getStringExtra(AUTH_RESPONSE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        toolbar.addSystemWindowInsetToPadding(top = true)
        discover.addSystemWindowInsetToMargin(bottom = true)

        if(signUp) {
            welcomeTitle.text = getString(R.string.all_set)
        }

        if (isAuthResponse()) {
            discover.text= getString(R.string.go_to, appName)
            title = getString(R.string.all_set)
            subTittle.isVisible = false
        }

        discover
            .clicks()
            .onEach {
                if (isAuthResponse()) {
                    sendAuthResponse()
                } else {
                    startActivity(HomepageActivity.getIntent(this))
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun isAuthResponse(): Boolean =
        redirectUrl != null && authResponseToken != null && appName != null

    private fun sendAuthResponse() {
        val uri = Uri.parse(redirectUrl)
            .buildUpon()
            .appendQueryParameter(IdentitiesActivity.AUTH_RESPONSE, authResponseToken)
            .build()

        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                uri
            )
        )

        finishAffinity()
    }

    companion object {

        const val SIGNUP = "signUP"
        const val APP_NAME = "appName"
        const val REDIRECT_URL = "redirectUrl"
        const val AUTH_RESPONSE = "authResponse"

        fun getIntent(context: Context, signUp: Boolean = false) =
            Intent(context, WelcomeActivity::class.java)
                .putExtra(SIGNUP, signUp)

        fun getIntent(
            context: Context,
            authResponse: AuthResponseModel,
            signUp: Boolean = false
        ) =
            Intent(context, WelcomeActivity::class.java)
                .putExtra(SIGNUP, signUp)
                .putExtra(APP_NAME, authResponse.appName)
                .putExtra(REDIRECT_URL, authResponse.redirectURL)
                .putExtra(AUTH_RESPONSE, authResponse.authResponseToken)

    }

}
