package org.stacks.app.ui.homepage

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import org.stacks.app.R
import org.stacks.app.ui.BaseActivity

@AndroidEntryPoint
class HomepageActivity : BaseActivity() {

    private lateinit var  viewModel: HomePageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)
        setContentView(R.layout.activity_homepage)
    }

}