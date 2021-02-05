package io.bloco.circles.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_how_it_works.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import io.bloco.circles.R
import io.bloco.circles.ui.BaseActivity
import reactivecircus.flowbinding.android.view.clicks

class HowItWorksActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_it_works)
        setNavigation()

        getStarted
            .clicks()
            .onEach {
                finish()
            }
            .launchIn(lifecycleScope)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, HowItWorksActivity::class.java)
    }

}
