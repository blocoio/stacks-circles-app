package org.stacks.app.ui.secret

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_secret_key.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import org.stacks.app.ui.BaseActivity
import org.stacks.app.ui.secret.bottomsheet.ShareKeyBottomSheetFragment
import reactivecircus.flowbinding.android.view.clicks

@AndroidEntryPoint
class SecretKeyActivity : BaseActivity() {

    private lateinit var viewModel: SecretKeyViewModel

    private val backButtonEnabled by lazy {
        intent?.getBooleanExtra(BACK_BUTTON, false) ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secret_key)
        if (backButtonEnabled) {
            setNavigation()
        }

        viewModel = ViewModelProvider(this).get(SecretKeyViewModel::class.java)

        viewModel
            .secretKey()
            .onEach {
                secretKey.text = it
            }
            .launchIn(lifecycleScope)

        copyKeyButton
            .clicks()
            .map { viewModel.copyPressed() }
            .onEach {
                ShareKeyBottomSheetFragment().apply {
                    show(supportFragmentManager, ShareKeyBottomSheetFragment.TAG)
                }
            }
            .launchIn(lifecycleScope)
    }

    companion object {

        const val BACK_BUTTON = "backButtonEnabled"

        fun getIntent(context: Context, backButtonEnabled: Boolean = false) =
            Intent(context, SecretKeyActivity::class.java)
                .putExtra(BACK_BUTTON, backButtonEnabled)
    }

}