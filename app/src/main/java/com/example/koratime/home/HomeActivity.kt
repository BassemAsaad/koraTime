package com.example.koratime.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.chat.ChatFragment
import com.example.koratime.database.updateUserLocationInFirestore
import com.example.koratime.databinding.ActivityHomeBinding
import com.example.koratime.friends.FriendsRequestsFragment
import com.example.koratime.rooms.TabsFragment
import com.example.koratime.stadiums_manager.StadiumsManagerFragment
import com.example.koratime.stadiums_user.StadiumsFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@Suppress("DEPRECATION")
class HomeActivity : BasicActivity<ActivityHomeBinding, HomeViewModel>(), HomeNavigator {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
        const val TAG = "HomeActivity"
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_home
    }

    override fun initViewModel(): HomeViewModel {
        return ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun openActivity() {

        dataBinding.homeBar.selectedItemId = R.id.home_bar
        replaceFragment(mainFragment())

        dataBinding.homeBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_bar -> {
                    replaceFragment(mainFragment())
                }
                R.id.rooms_bar -> {
                    replaceFragment(TabsFragment())
                }
                R.id.friends_bar -> {
                    replaceFragment(FriendsRequestsFragment())
                    }
                R.id.chat_bar -> {
                    replaceFragment(ChatFragment())
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel
        getLocationIfPermissionGranted()
        openActivity()
    }

    private fun mainFragment(): Fragment {
        return if (DataUtils.user?.nationalID == null){
            StadiumsFragment()
        }else{
            StadiumsManagerFragment()
        }
    }
    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val push = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            push.addToBackStack("")
        }
        push.commit()
    }
    fun addFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val push = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            push.addToBackStack("")
        }
        push.commit()
    }


    private fun getLocationIfPermissionGranted() {
        if (hasLocationPermissions()) {
            getLastKnownLocation()
        } else {
            requestLocationPermissions()
        }
    }
    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Retrieve city name based on latitude and longitude
                    val cityName = getCityName(latitude, longitude)

                    // Update user's location in Firestore along with city name
                    updateUserLocationInFirestore(
                        DataUtils.user!!.id!!, latitude, longitude, cityName,
                        onSuccessListener = {
                            Log.e("Firestore", "Location updated in Firestore")
                        },
                        onFailureListener = {
                            Log.e("Firestore", "Error updating location in Firestore")
                        }
                    )
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to get last known location.", Toast.LENGTH_SHORT)
                    .show()
                Log.e("Location", " location is null: : ", e)
            }
    }

    private fun getCityName(latitude: Double, longitude: Double): String {
        val geoCoder = Geocoder(this)
        try {
            val addressList = geoCoder.getFromLocation(latitude, longitude, 1)
            val firstAddress = addressList!![0]
            val address = firstAddress.getAddressLine(0)

            return address

        } catch (e: Exception) {
            Log.e(TAG, " address from latitude and longitude: ", e)
            return e.toString()
        }
    }



    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) { // Check if the request code matches the location permission request code
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { // Check if permissions are granted
               getLastKnownLocation() // If permissions are granted, attempt to get the last known location
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT)
                    .show() // Show a toast message indicating permission denial
            }
        }
    }


}