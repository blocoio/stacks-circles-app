package org.stacks.app.ui.auth.signup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_username.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import org.stacks.app.data.AuthResponse
import org.stacks.app.ui.BaseActivity
import org.stacks.app.ui.auth.WelcomeActivity
import org.stacks.app.ui.auth.identities.IdentitiesActivity
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.textChanges

@AndroidEntryPoint
class ChooseUsernameActivity : BaseActivity() {

    private val viewModel: ChooseUsernameViewModel by lazy {
        ViewModelProvider(this).get(ChooseUsernameViewModel::class.java)
    }

    private val signUp by lazy {
        intent?.getBooleanExtra(SIGN_UP, false) ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)

        if (!signUp) {
            setNavigation()
        }

        viewModel.signUp = signUp

        username
            .textChanges()
            .onEach {
                outlinedTextField.error = null
            }
            .launchIn(lifecycleScope)

        continueBtn
            .clicks()
            .onEach {
                viewModel.usernamePicked(username.text.toString())
            }
            .launchIn(lifecycleScope)

        viewModel
            .openNewAccountScreen()
            .onEach { startActivity(WelcomeActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        viewModel
            .loading()
            .onEach { loading ->
                loadingSpinner.isVisible = loading
                continueBtn.isVisible = !loading
                chooseUsernameLayout.isVisible = !loading
            }
            .launchIn(lifecycleScope)

        viewModel
            .sendAuthResponse()
            .onEach {

                if (signUp) {
                    startActivity(
                        WelcomeActivity.getIntent(
                            this,
                            it.appName,
                            it.redirectURL,
                            it.authResponseToken
                        )
                    )
                } else {
                    sendAuthResponse(it)
                }
            }
            .launchIn(lifecycleScope)

        viewModel
            .errors()
            .onEach { error ->
                outlinedTextField.error = when (error) {
                    ChooseUsernameViewModel.Errors.UnavailableUsername -> getString(R.string.invalidUsername)
                    ChooseUsernameViewModel.Errors.SignUpError -> getString(R.string.error)
                }

            }
            .launchIn(lifecycleScope)
    }

    private fun sendAuthResponse(authResponse: AuthResponse) {
        val uri = Uri.parse(authResponse.redirectURL)
            .buildUpon()
            .appendQueryParameter(IdentitiesActivity.AUTH_RESPONSE, authResponse.authResponseToken)
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

        const val SIGN_UP = "signUp"

        fun getIntent(context: Context, signUp: Boolean = false) =
            Intent(context, ChooseUsernameActivity::class.java)
                .putExtra(SIGN_UP, signUp)
    }
}
