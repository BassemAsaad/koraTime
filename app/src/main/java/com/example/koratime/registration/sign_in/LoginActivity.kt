package com.example.koratime.registration.sign_in
import android.content.Intent
import android.os.Bundle

import androidx.lifecycle.ViewModelProvider
import com.example.koratime.basic.BasicActivity
import com.example.koratime.R
import com.example.koratime.databinding.ActivityLoginBinding
import com.example.koratime.registration.create_account.RegisterActivity
import com.example.koratime.home.HomeActivity


class LoginActivity
    : BasicActivity<ActivityLoginBinding, Login_ViewModel>(),LoginNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }

    override fun initView() {
        dataBinding.loginVM = viewModel
        viewModel.navigator = this
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_login
    }
    override fun initViewModel(): Login_ViewModel {
        return ViewModelProvider(this).get(Login_ViewModel::class.java)
    }

    override fun openHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
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