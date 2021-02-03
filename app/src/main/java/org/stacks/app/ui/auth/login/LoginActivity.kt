package org.stacks.app.ui.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import org.stacks.app.ui.BaseActivity
import org.stacks.app.ui.auth.WelcomeActivity
import org.stacks.app.ui.auth.identities.IdentitiesActivity
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.textChanges

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        secretKey
            .textChanges()
            .onEach {
                outlinedTextField.error = null
                viewModel.secretKeyUpdated(it)
            }
            .launchIn(lifecycleScope)

        signInButton
            .clicks()
            .onEach { viewModel.submitClicked(secretKey.text.toString()) }
            .launchIn(lifecycleScope)

        viewModel
            .showError()
            .onEach { outlinedTextField.error = getString(R.string.secret_key_error) }
            .launchIn(lifecycleScope)

        viewModel
            .openIdentitiesScreen()
            .onEach { startActivity(IdentitiesActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        viewModel
            .openWelcomeScreen()
            .onEach { startActivity(WelcomeActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        viewModel
            .loading()
            .onEach { loading ->
                loadingSpinner.isVisible = loading
                signInButton.isVisible = !loading
                secretKey.isVisible = !loading
                secretKeyMessage.isVisible = !loading
                secretKeyTitle.isVisible = !loading
            }
            .launchIn(lifecycleScope)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

}