package org.stacks.app.ui.auth.identities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import org.stacks.app.ui.shared.IdentityRowView
import timber.log.Timber

@AndroidEntryPoint
class IdentitiesActivity : BaseActivity() {

    private val viewModel: IdentitiesViewModel by lazy {
        ViewModelProvider(this).get(IdentitiesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identities)

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
                Timber.i(it)
            }
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
        fun getIntent(context: Context) = Intent(context, IdentitiesActivity::class.java)
    }

}
