package org.stacks.app.ui.auth.identities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_account.accounts
import kotlinx.android.synthetic.main.activity_identities.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.stacks.app.R
import org.stacks.app.data.IdentityModel
import org.stacks.app.ui.BaseActivity
import org.stacks.app.ui.auth.signup.ChooseUsernameActivity
import org.stacks.app.ui.shared.IdentityRowView
import reactivecircus.flowbinding.android.view.clicks

@AndroidEntryPoint
class IdentitiesActivity : BaseActivity() {

    private val viewModel: IdentitiesViewModel by lazy {
        ViewModelProvider(this).get(IdentitiesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identities)

        newIdentity
            .clicks()
            .onEach { startActivity(ChooseUsernameActivity.getIntent(this, true)) }
            .launchIn(lifecycleScope)

        viewModel
            .identities()
            .onEach { setIdentitiesRows(it) }
            .launchIn(lifecycleScope)

        viewModel
            .appDetails()
            .onEach {
                imageLoader.loadAppIcon(appIcon, it.icon)
                appName.isVisible = true
                appName.text = it.name
            }
            .launchIn(lifecycleScope)

        viewModel
            .sendAuthResponse()
            .onEach {

                val uri = Uri.parse(it.redirectURL)
                    .buildUpon()
                    .appendQueryParameter(AUTH_RESPONSE, it.token)
                    .build()

                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        uri
                    )
                )

                finishAffinity()
            }
            .launchIn(lifecycleScope)

        viewModel
            .errors()
            .onEach { Snackbar.make(root, getString(R.string.error), Snackbar.LENGTH_LONG).show() }
            .launchIn(lifecycleScope)

        appConnectionDescription.text = getString(R.string.to_connect_to, viewModel.appDomain())
    }


    private suspend fun setIdentitiesRows(identities: List<IdentityModel>) {
        accounts.removeAllViews()
        identities.forEach { identityModel ->
            val identityRowView = IdentityRowView(this, identityModel)

            identityRowView.setOnClickListener {
                GlobalScope.launch {
                    viewModel.identitySelected(identityModel)
                }
            }

            accounts.addView(identityRowView)
        }
    }

    companion object {
        const val AUTH = 2
        const val AUTH_RESPONSE = "authResponse"

        fun getIntent(context: Context) = Intent(context, IdentitiesActivity::class.java)
    }

}
