package com.example.koratime.home.home_manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.chat.ChatFragment
import com.example.koratime.databinding.ActivityHomeManagerBinding
import com.example.koratime.friends.FriendsRequestsFragment
import com.example.koratime.rooms.TabsFragment
import com.example.koratime.stadiums_manager.StadiumsManagerFragment

class HomeManagerActivity : BasicActivity<ActivityHomeManagerBinding, HomeManagerViewModel>(),
    HomeManagerNavigator {

    override fun getLayoutID(): Int {
        return R.layout.activity_home_manager
    }

    override fun initViewModel(): HomeManagerViewModel {
        return ViewModelProvider(this)[HomeManagerViewModel::class.java]
    }

    override fun openActivity() {
        dataBinding.managerHomeBar.selectedItemId = R.id.stadium_bar
        pushFragment(StadiumsManagerFragment())

        dataBinding.managerHomeBar.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.stadium_bar) {
                pushFragment(StadiumsManagerFragment())
            }
            if (item.itemId == R.id.rooms_bar) {
                pushFragment(TabsFragment())
            }
            if (item.itemId == R.id.friends_bar) {
                pushFragment(FriendsRequestsFragment())
            }
            if (item.itemId == R.id.chat_bar) {
                pushFragment(ChatFragment())
            }
            return@setOnItemSelectedListener true
        }
    }

    override fun LogoutActivity() {
        TODO("Not yet implemented")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        openActivity()
    }

    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel
    }

    private fun pushFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("")
            .commit()
    }


}