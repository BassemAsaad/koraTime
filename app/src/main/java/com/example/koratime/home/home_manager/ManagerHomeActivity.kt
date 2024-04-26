package com.example.koratime.home.home_manager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.updateLocationInFirestore
import com.example.koratime.databinding.ActivityManagerHomeBinding
import com.example.koratime.stadiums_manager.StadiumsManagerFragment
import com.example.koratime.stadiums_manager.createStadium.AddStadiumActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.Locale

class ManagerHomeActivity : BasicActivity<ActivityManagerHomeBinding,ManagerHomeViewModel>(),ManagerHomeNavigator {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

        private val LOCATION_PERMISSION_REQUEST_CODE = 100

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
        openActivity()
    }

    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel
        getLocationIfPermissionGranted()
    }

    private fun pushFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .addToBackStack("")
            .commit()
    }


    private fun getLocationIfPermissionGranted() {
        if (hasLocationPermissions()) {
            getLastKnownLocation()
        } else {
            requestLocationPermissions()
        }
    }
    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Retrieve city name based on latitude and longitude
                    val cityName = getCityName(latitude, longitude)

                    // Update user's location in Firestore along with city name
                    val userId = Firebase.auth.currentUser?.uid

                    if (userId != null) {
                        updateLocationInFirestore(userId, latitude, longitude, cityName,
                            onSuccessListener = {
                                Log.e("Firestore", "Location updated in Firestore")
                            },
                            onFailureListener = {
                                Log.e("Firestore", "Error updating location in Firestore")
                            }
                        )
                    } else {
                        Log.e("Firestore", "Failed to update location: User ID is null")
                    }
                } else {
                    Toast.makeText(this, "Last known location is not available.",
                        Toast.LENGTH_SHORT).show()
                } }//end of onSuccess
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get last known location.",
                    Toast.LENGTH_SHORT).show()
            }


    }
    private fun getCityName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return if (addresses!!.isNotEmpty()) {
            addresses[0].locality ?: addresses[0].adminArea ?: ""
        } else {
            "null"
        }
    }
    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) { // Check if the request code matches the location permission request code
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { // Check if permissions are granted
                getLastKnownLocation() // If permissions are granted, attempt to get the last known location
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show() // Show a toast message indicating permission denial
            }
        }
    }

}