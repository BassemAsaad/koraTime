package com.example.koratime.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

class LocationActivity : BasicActivity<ActivityLocationBinding, LocationPickerViewModel>() {

    override fun getLayoutID(): Int {
        return R.layout.activity_location
    }
    override fun initViewModel(): LocationPickerViewModel {
        return ViewModelProvider(this)[LocationPickerViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }


    override fun initView() {

        getLocation()

    }
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    fun getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this ,Manifest.permission.ACCESS_COARSE_LOCATION)
            !=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission is not Granted", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100 )
        }

        //get latitude and longitude
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null){
                val latitude = it.latitude
                val longitude =  it.longitude

                getSpecificPlaceName(latitude, longitude)

                dataBinding.latitudeTextview.text = latitude.toString()
                dataBinding.longitudeTextview.text = longitude.toString()

                Toast.makeText(this, "Location Works", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show()
            }}
    }

    private fun getSpecificPlaceName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        var cityName = ""
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses!!.isNotEmpty()) {
                cityName = addresses[0].locality ?: ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cityName
    }


}