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
import kotlinx.android.synthetic.main.partial_tool_bar.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import org.stacks.app.data.AuthResponseModel
import org.stacks.app.ui.BaseActivity
import org.stacks.app.ui.auth.WelcomeActivity
import org.stacks.app.ui.auth.identities.IdentitiesActivity
import org.stacks.app.ui.shared.Insets.addSystemWindowInsetToMargin
import org.stacks.app.ui.shared.Insets.addSystemWindowInsetToPadding
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
        toolbar.addSystemWindowInsetToPadding(top = true)
        continueBtn.addSystemWindowInsetToMargin(bottom = true)

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
            .onEach { startActivity(WelcomeActivity.getIntent(this, signUp)) }
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
                    startActivity(WelcomeActivity.getIntent(this, it))
                } else {
                    sendAuthResponse(it)
                }
            }
            .launchIn(lifecycleScope)

        viewModel
            .errors()
            .onEach { error ->
                outlinedTextField.error = when (error) {
                    ChooseUsernameViewModel.Error.UnavailableUsername -> getString(R.string.username_not_available)
                    ChooseUsernameViewModel.Error.SignUpError -> getString(R.string.error)
                    ChooseUsernameViewModel.Error.InvalidUsername -> getString(R.string.invalid_username)
                }

            }
            .launchIn(lifecycleScope)
    }

    private fun sendAuthResponse(authResponseModel: AuthResponseModel) {
        val uri = Uri.parse(authResponseModel.redirectURL)
            .buildUpon()
            .appendQueryParameter(
                IdentitiesActivity.AUTH_RESPONSE,
                authResponseModel.authResponseToken
            )
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
