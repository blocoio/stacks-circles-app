package org.stacks.app.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import org.stacks.app.R
import org.stacks.app.ui.BaseActivity

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        setContentView(R.layout.activity_login)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

}