package com.example.koratime.registration.log_in

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityLoginBinding
import com.example.koratime.home.home_manager.HomeManagerActivity
import com.example.koratime.home.home_user.HomeActivity
import com.example.koratime.registration.create_account.RegisterActivity


class LoginActivity
    : BasicActivity<ActivityLoginBinding, LoginViewModel>(), LoginNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }

    override fun initView() {
        dataBinding.loginVM = viewModel
        viewModel.navigator = this
        openRegisterActivity()
        viewModel.toastMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
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

    override fun openHomeManagerActivity() {
        val intent = Intent(this, HomeManagerActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun openRegisterActivity() {
        dataBinding.signUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

}