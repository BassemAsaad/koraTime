package com.example.koratime.home.home_manager

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityManagerHomeBinding
import com.example.koratime.stadiums_manager.StadiumsManagerFragment
import com.example.koratime.stadiums_manager.createStadium.AddStadiumActivity

class ManagerHomeActivity : BasicActivity<ActivityManagerHomeBinding,ManagerHomeViewModel>(),ManagerHomeNavigator {
    override fun getLayoutID(): Int {
        return R.layout.activity_manager_home
    }

    override fun initViewModel(): ManagerHomeViewModel {
        return ViewModelProvider(this)[ManagerHomeViewModel::class.java]
    }
    override fun openActivity() {
        dataBinding.managerHomeBar.selectedItemId = R.id.stadium_bar
        pushFragment(StadiumsManagerFragment())

        dataBinding.managerHomeBar.setOnItemSelectedListener {item->
            if (item.itemId == R.id.stadium_bar){
                pushFragment(StadiumsManagerFragment())
            }
            if (item.itemId == R.id.notification_bar){
                pushFragment(StadiumsManagerFragment())
            }
            return@setOnItemSelectedListener true
        }
    }

    override fun LogoutActivity() {
        TODO("Not yet implemented")
    }

    override fun createStadiumActivity() {
        val intent = Intent(this,AddStadiumActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel
    }

    private fun pushFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .addToBackStack("")
            .commit()
    }


}