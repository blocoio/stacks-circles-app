package io.bloco.circles.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import io.bloco.circles.R
import io.bloco.circles.data.IdentityModel
import io.bloco.circles.ui.BaseActivity
import io.bloco.circles.ui.auth.signup.ChooseUsernameActivity
import io.bloco.circles.ui.homepage.HomepageActivity
import io.bloco.circles.ui.secret.SecretKeyActivity
import io.bloco.circles.ui.shared.IdentityRowView
import reactivecircus.flowbinding.android.view.clicks

@AndroidEntryPoint
class AccountActivity : BaseActivity() {

    private val viewModel: AccountViewModel by lazy {
        ViewModelProvider(this).get(AccountViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        setNavigation()

        viewModel
            .identities()
            .onEach { setIdentitiesRows(it) }
            .launchIn(lifecycleScope)

        viewModel
            .loggedOut()
            .onEach { startActivity(HomepageActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        viewModel
            .finish()
            .onEach { finish() }
            .launchIn(lifecycleScope)

        newIdentity
            .clicks()
            .onEach { startActivity(ChooseUsernameActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        viewSecretKey
            .clicks()
            .onEach { startActivity(SecretKeyActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        logout
            .clicks()
            .onEach { viewModel.logoutPressed() }
            .launchIn(lifecycleScope)

    }

    private fun setIdentitiesRows(identities: List<IdentityModel>) {
        accounts.removeAllViews()
        identities.forEach {
            accounts.addView(IdentityRowView(this, it))
        }
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, AccountActivity::class.java)
    }

}
