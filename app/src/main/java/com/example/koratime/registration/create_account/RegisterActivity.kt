package com.example.koratime.registration.create_account

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.basic.BasicActivity
import com.example.koratime.R
import com.example.koratime.basic.Navigator
import com.example.koratime.databinding.ActivityRegisterBinding
import com.example.koratime.registration.sign_in.LoginActivity
import com.example.koratime.home.HomeActivity

class RegisterActivity : BasicActivity<ActivityRegisterBinding, Register_ViewModel>(),Navigator {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

    }

    override fun initView() {
        dataBinding.registerVM = viewModel
        viewModel.navigator = this
        dataBinding.signIn.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            viewModel.showLoading.value=true
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_register
    }
    override fun initViewModel(): Register_ViewModel {
        return ViewModelProvider(this).get(Register_ViewModel::class.java)
    }

    override fun openHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        viewModel.showLoading.value=true
    }
}