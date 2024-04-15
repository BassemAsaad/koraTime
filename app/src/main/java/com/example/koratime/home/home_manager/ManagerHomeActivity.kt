package com.example.koratime.home.home_manager

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityManagerHomeBinding

class ManagerHomeActivity : BasicActivity<ActivityManagerHomeBinding,ManagerHomeViewModel>(),ManagerHomeNavigator {
    override fun getLayoutID(): Int {
        return R.layout.activity_manager_home
    }

    override fun initViewModel(): ManagerHomeViewModel {
        return ViewModelProvider(this)[ManagerHomeViewModel::class.java]
    }
    override fun openActivity() {
        TODO("Not yet implemented")
    }

    override fun LogoutActivity() {
        TODO("Not yet implemented")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel
    }




}