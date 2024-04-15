package com.example.koratime.registration.create_account

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.basic.BasicActivity
import com.example.koratime.R
import com.example.koratime.databinding.ActivityRegisterPlayerBinding
import com.example.koratime.registration.log_in.LoginActivity
import com.example.koratime.home.HomeActivity

class RegisterActivity : BasicActivity<ActivityRegisterPlayerBinding, RegisterViewModel>(),RegisterNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }

    override fun initView() {
        dataBinding.registerVM = viewModel
        viewModel.navigator = this

    }

    override fun getLayoutID(): Int {
        return R.layout.activity_register_player
    }
    override fun initViewModel(): RegisterViewModel {
        return ViewModelProvider(this).get(RegisterViewModel::class.java)
    }
    override fun openLoginActivity() {
        dataBinding.logIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        viewModel.showLoading.value=true
    }

    override fun openHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        viewModel.showLoading.value=true
    }
}