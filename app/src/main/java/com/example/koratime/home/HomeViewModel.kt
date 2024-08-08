package com.example.koratime.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.updateUserLocationInFirestore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class HomeViewModel : BasicViewModel<HomeNavigator>() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(context: Context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Retrieve city name based on latitude and longitude
                    val cityName = getCityName(context, latitude, longitude)

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
                Toast.makeText(context, "Failed to get last known location.", Toast.LENGTH_SHORT)
                    .show()
                Log.e("Location", " location is null: : ", e)
            }
    }

    fun getCityName(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses!!.isNotEmpty()) {
            val address = addresses[0].locality ?: addresses[0].adminArea ?: ""
            Log.e("City", "$address ")
            return address

        } else {
            return "null"
        }
    }

    fun hasLocationPermissions(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

}