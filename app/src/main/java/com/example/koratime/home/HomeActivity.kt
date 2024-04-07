package com.example.koratime.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.chat.ChatFragment
import com.example.koratime.databinding.ActivityHomeBinding
import com.example.koratime.model.UserModel
import com.example.koratime.stadiums.Stadiums_Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.util.Locale

class HomeActivity : BasicActivity<ActivityHomeBinding, HomeViewModel>() ,HomeNavigator{

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val auth= Firebase.auth
    private val handler = Handler()
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
    override fun LogoutActivity() {
        TODO("Not yet implemented")
    }
    override fun getLayoutID(): Int {
        return R.layout.activity_home
    }
    override fun initViewModel(): HomeViewModel {
        return ViewModelProvider(this)[HomeViewModel::class.java]
    }
    override fun onStart() {
        super.onStart()
        updateLocationEvery10Seconds()
    }
    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }

    override fun initView() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationIfPermissionGranted()
        openActivity()
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
    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                // Retrieve city name based on latitude and longitude
                val cityName = getCityName(latitude, longitude)

                // Update user's location in Firestore along with city name
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    updateLocationInFirestore(userId, latitude, longitude, cityName)
                } else {
                    Toast.makeText(this, "Failed to update location: User ID is null", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Last known location is not available.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get last known location.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getCityName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return if (addresses!!.isNotEmpty()) {
            addresses[0].locality ?: addresses[0].adminArea ?: ""
        } else {
            ""
        }
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
    private fun updateLocationInFirestore(userId: String, latitude: Double, longitude: Double, cityName: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection(UserModel.collectionName).document(userId)
        userRef.update(
            mapOf(
                "latitude" to latitude,
                "longitude" to longitude,
                "city" to cityName
            )
        ).addOnSuccessListener {
            // Handle successful update
            Toast.makeText(this, "Location updated in Firestore", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            // Handle failure
            Toast.makeText(this, "Failed to update location in Firestore", Toast.LENGTH_SHORT).show()
            Log.e("Firestore", "Error updating location in Firestore", e)
        }
    }
    private fun updateLocationEvery10Seconds() {
        handler.post(object : Runnable {
            override fun run() {
                getLastKnownLocation()
                handler.postDelayed(this, 10 * 1000) // Repeat every 10 seconds
            }
        })
    }

}