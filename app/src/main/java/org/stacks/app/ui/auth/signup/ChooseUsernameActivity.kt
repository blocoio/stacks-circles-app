package org.stacks.app.ui.auth.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_username.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import org.stacks.app.ui.BaseActivity
import org.stacks.app.ui.auth.WelcomeActivity
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

        username
            .textChanges()
            .onEach {
                outlinedTextField.error = null
            }
            .launchIn(lifecycleScope)

        continueBtn
            .clicks()
            .onEach {
                viewModel.usernamePicked(username.text.toString(), signUp)
            }
            .launchIn(lifecycleScope)

        viewModel
            .openNewAccountScreen()
            .onEach {
                startActivity(WelcomeActivity.getIntent(this))
            }
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
            .errors()
            .onEach { error ->
                outlinedTextField.error = when (error) {
                    ChooseUsernameViewModel.Errors.UnavailableUsername -> getString(R.string.invalidUsername)
                    ChooseUsernameViewModel.Errors.SignUpError -> getString(R.string.error)
                }

            }
            .launchIn(lifecycleScope)
    }

    companion object {

        const val SIGN_UP = "signUp"

        fun getIntent(context: Context, signUp: Boolean = false) =
            Intent(context, ChooseUsernameActivity::class.java)
                .putExtra(SIGN_UP, signUp)
    }
}
