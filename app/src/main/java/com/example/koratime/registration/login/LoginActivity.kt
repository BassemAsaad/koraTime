package com.example.koratime.registration.login

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityLoginBinding
import com.example.koratime.home.HomeActivity
import com.example.koratime.registration.createAccount.RegisterActivity


class LoginActivity
    : BasicActivity<ActivityLoginBinding, LoginViewModel>(), LoginNavigator {
    override val TAG: String
        get() = "LoginActivity"

    override fun initView() {
        callback()
    }

    override fun callback() {
        viewModel.apply {
            navigator = this@LoginActivity
            toastMessage.observe(this@LoginActivity) { message ->
                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
            }
        }

        dataBinding.apply {
            loginVM = viewModel
            signUp.setOnClickListener {
                openRegisterActivity()
            }
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_login
    }

    override fun initViewModel(): LoginViewModel {
        return ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun openHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun openRegisterActivity() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }
}