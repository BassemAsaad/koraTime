package com.example.koratime.home.home_user

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.chat.ChatFragment
import com.example.koratime.databinding.ActivityHomeBinding
import com.example.koratime.friends.FriendsRequestsFragment
import com.example.koratime.rooms.TabsFragment
import com.example.koratime.stadiums_user.StadiumsFragment

@Suppress("DEPRECATION")
class HomeActivity : BasicActivity<ActivityHomeBinding, HomeViewModel>() , HomeNavigator {

    companion object{
        val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
    override fun getLayoutID(): Int {
        return R.layout.activity_home
    }
    override fun initViewModel(): HomeViewModel {
        return ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun openActivity() {
        dataBinding.homeBar.selectedItemId = R.id.home_bar
        pushFragment(StadiumsFragment())

        dataBinding.homeBar.setOnItemSelectedListener { item->
            if (item.itemId == R.id.chat_bar){
                pushFragment(ChatFragment())
            }
            if (item.itemId == R.id.home_bar){
                pushFragment(StadiumsFragment())
            }
            if (item.itemId == R.id.rooms_bar){
                pushFragment(TabsFragment())
            }
            if (item.itemId == R.id.friends_bar){
                pushFragment(FriendsRequestsFragment())
            }
            return@setOnItemSelectedListener true
        }
    }
    override fun LogoutActivity() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }
    override fun initView() {
        viewModel.navigator=this
        dataBinding.vm=viewModel

        getLocationIfPermissionGranted()
        openActivity()


    }
    private fun pushFragment(fragment: Fragment, addToBackStack:Boolean=false){
        val push = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
        if(addToBackStack){
            push.addToBackStack("name")
        }
        push.commit()

    }
    private fun getLocationIfPermissionGranted() {
        if (viewModel.hasLocationPermissions(this)) {
            viewModel.getLastKnownLocation(this)
        } else {
            requestLocationPermissions()
        }
    }
    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) { // Check if the request code matches the location permission request code
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { // Check if permissions are granted
                viewModel.getLastKnownLocation(this) // If permissions are granted, attempt to get the last known location
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show() // Show a toast message indicating permission denial
            }
        }
    }



}