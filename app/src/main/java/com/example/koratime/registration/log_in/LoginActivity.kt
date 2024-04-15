package com.example.koratime.registration.log_in
import android.content.Intent
import android.os.Bundle

import androidx.lifecycle.ViewModelProvider
import com.example.koratime.basic.BasicActivity
import com.example.koratime.R
import com.example.koratime.databinding.ActivityLoginBinding
import com.example.koratime.home.home_manager.ManagerHomeActivity
import com.example.koratime.registration.create_account.RegisterActivity
import com.example.koratime.home.home_user.HomeActivity


class LoginActivity
    : BasicActivity<ActivityLoginBinding, LoginViewModel>(),LoginNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }

    override fun initView() {
        dataBinding.loginVM = viewModel
        viewModel.navigator = this
        openRegisterActivity()
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
        viewModel.showLoading.value=true
    }

    override fun openHomeManagerActivity() {
        val intent = Intent(this, ManagerHomeActivity::class.java)
        startActivity(intent)
        viewModel.showLoading.value=true
    }

    override fun openRegisterActivity() {
        dataBinding.signUp.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            viewModel.showLoading.value=true
        }
    }

}