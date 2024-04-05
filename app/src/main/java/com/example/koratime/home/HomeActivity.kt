package com.example.koratime.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.chat.ChatFragment
import com.example.koratime.databinding.ActivityHomeBinding
import com.example.koratime.stadiums.Stadiums_Fragment

class HomeActivity : BasicActivity<ActivityHomeBinding, HomeViewModel>() ,HomeNavigator{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }



    override fun getLayoutID(): Int {
        return R.layout.activity_home
    }

    override fun initViewModel(): HomeViewModel {
        return ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun initView() {
        openActivity()





    }

    private fun pushFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .addToBackStack("")
            .commit()
    }

    override fun openActivity() {
        dataBinding.homeBar.selectedItemId = R.id.home_bar
        pushFragment(Stadiums_Fragment())

        dataBinding.homeBar.setOnItemSelectedListener {item->
            if (item.itemId == R.id.chat_bar){
                pushFragment(ChatFragment())
            }
            if (item.itemId == R.id.home_bar){
                pushFragment(Stadiums_Fragment())
            }

            return@setOnItemSelectedListener true
        }
    }

    override fun openLoginActivity() {
        TODO("Not yet implemented")
    }


}