package org.stacks.app.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.partial_tool_bar.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import org.stacks.app.data.IdentityModel
import org.stacks.app.ui.BaseActivity

@AndroidEntryPoint
class AccountActivity : BaseActivity() {

    private lateinit var viewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        toolbarTitle.text = getString(R.string.connect_settings)

        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        viewModel
            .identities()
            .onEach(::setIdentitiesRows)
            .launchIn(lifecycleScope)

    }

    private fun setIdentitiesRows(identities: List<IdentityModel>) {
        identities.forEach {
            accounts.addView(IdentityRowView(this, it))
        }
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, AccountActivity::class.java)
    }

}