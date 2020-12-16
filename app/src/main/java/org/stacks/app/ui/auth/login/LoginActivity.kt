package org.stacks.app.ui.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import org.stacks.app.ui.BaseActivity
import org.stacks.app.ui.auth.WelcomeActivity
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.textChanges

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        secretKey
            .textChanges()
            .onEach {
                viewModel.secretKeyUpdated(it)
            }
            .launchIn(lifecycleScope)

        signInButton
            .clicks()
            .onEach {
                viewModel.submitSecretKey(secretKey.text.toString())
            }
            .launchIn(lifecycleScope)

        viewModel
            .secretKeyState()
            .onEach {
                outlinedTextField.error = if (it is LoginViewModel.LoginState.InvalidSecretKey) {
                    getString(R.string.secret_key_error)
                } else {
                    null
                }
            }
            .launchIn(lifecycleScope)

        viewModel
            .secretKeyState()
            .filter { it is LoginViewModel.LoginState.ValidSecretKey }
            .onEach {
                startActivity(WelcomeActivity.getIntent(this))
            }
            .launchIn(lifecycleScope)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

}