package org.stacks.app.ui.homepage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.partial_learn_about_us.*
import kotlinx.android.synthetic.main.partial_newsletter.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import org.stacks.app.ui.BaseActivity
import org.stacks.app.ui.auth.ConnectActivity
import reactivecircus.flowbinding.android.view.clicks


@AndroidEntryPoint
class HomepageActivity : BaseActivity() {

    private lateinit var viewModel: HomePageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)
        setContentView(R.layout.activity_homepage)

        startHere
            .clicks()
            .onEach {
                startActivity(ConnectActivity.getIntent(this))
            }
            .launchIn(lifecycleScope)

        aboutUsCard
            .clicks()
            .onEach {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_us_url))))
            }
            .launchIn(lifecycleScope)

        newsletterSubscribe
            .clicks()
            .onEach {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.newsletter_url))))
            }
            .launchIn(lifecycleScope)
    }


}