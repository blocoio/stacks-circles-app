package org.stacks.app.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.stacks.app.R
import org.stacks.app.ui.auth.ConnectActivity
import org.stacks.app.ui.auth.identities.IdentitiesActivity
import org.stacks.app.ui.homepage.HomepageActivity

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private val viewModel: SplashViewModel by lazy {
        ViewModelProvider(this).get(SplashViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        GlobalScope.launch {
            delay(SPLASH_SCREEN_DELAY_MILLISECONDS)
            viewModel
                .dataReceived(intent?.data)
        }

        viewModel
            .openHomepage()
            .onEach { startActivity(HomepageActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        viewModel
            .openConnect()
            .onEach { startActivity(ConnectActivity.getIntent(this)) }
            .launchIn(lifecycleScope)

        viewModel
            .openIdentities()
            .onEach { startActivityForResult(IdentitiesActivity.getIntent(this), IdentitiesActivity.AUTH) }
            .launchIn(lifecycleScope)

        viewModel
            .errors()
            .onEach { startActivity(HomepageActivity.getIntent(this, true)) }
            .launchIn(lifecycleScope)
    }

    companion object {
        const val RESULT_OK = 0
        const val RESULT_ERROR = -1

        const val SPLASH_SCREEN_DELAY_MILLISECONDS = 1 * 1000L
    }

}