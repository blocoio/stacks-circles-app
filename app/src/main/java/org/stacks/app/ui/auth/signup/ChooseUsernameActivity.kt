package org.stacks.app.ui.auth.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_username.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import org.stacks.app.ui.BaseActivity
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.textChanges

@AndroidEntryPoint
class ChooseUsernameActivity : BaseActivity() {

    private val viewModel: ChooseUsernameViewModel by lazy {
        ViewModelProvider(this).get(ChooseUsernameViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)

        username
            .textChanges()
            .filter { outlinedTextField.error != null }
            .onEach {
                outlinedTextField.error = null
            }
            .launchIn(lifecycleScope)

        continueBtn
            .clicks()
            .onEach {
                viewModel.submitUsername(username.text.toString())
            }
            .launchIn(lifecycleScope)

        viewModel
            .errors()
            .onEach {
                outlinedTextField.error = "Something went wrong"
            }
            .launchIn(lifecycleScope)
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, ChooseUsernameActivity::class.java)
    }

}
