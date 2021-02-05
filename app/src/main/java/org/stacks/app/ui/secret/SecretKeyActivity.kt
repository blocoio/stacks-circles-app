package org.stacks.app.ui.secret

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_secret_key.*
import kotlinx.android.synthetic.main.partial_tool_bar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.stacks.app.R
import org.stacks.app.ui.BaseActivity
import org.stacks.app.ui.secret.bottomsheet.ShareKeyBottomSheetFragment
import org.stacks.app.ui.shared.Insets.addSystemWindowInsetToMargin
import org.stacks.app.ui.shared.Insets.addSystemWindowInsetToPadding
import reactivecircus.flowbinding.android.view.clicks

@AndroidEntryPoint
class SecretKeyActivity : BaseActivity() {

    private val viewModel: SecretKeyViewModel by lazy {
        ViewModelProvider(this).get(SecretKeyViewModel::class.java)
    }

    private val signUp by lazy {
        intent?.getBooleanExtra(SIGN_UP, false) ?: false
    }

    private val shareBottomSheetFragment = ShareKeyBottomSheetFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secret_key)
        copyKeyButton.addSystemWindowInsetToMargin(bottom = true)
        toolbar.addSystemWindowInsetToPadding(top = true)
        setNavigation()

        if (signUp) {
            shareBottomSheetFragment.signUp = signUp

            GlobalScope.launch(Dispatchers.Main) {
                viewModel.signUp()
            }
        }

        viewModel
            .secretKey()
            .onEach {
                secretKey.text = it
            }
            .launchIn(lifecycleScope)

        copyKeyButton
            .clicks()
            .onEach {
                viewModel.copyPressed()
                shareBottomSheetFragment.show(
                    supportFragmentManager,
                    ShareKeyBottomSheetFragment.TAG
                )
            }
            .launchIn(lifecycleScope)
    }

    companion object {

        const val SIGN_UP = "signUp"

        fun getIntent(context: Context, signUp: Boolean = false) =
            Intent(context, SecretKeyActivity::class.java)
                .putExtra(SIGN_UP, signUp)
    }

}