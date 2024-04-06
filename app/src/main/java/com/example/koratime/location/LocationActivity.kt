package com.example.koratime.location

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices



class LocationActivity : BasicActivity<ActivityLocationBinding, LocationViewModel>() {

    override fun getLayoutID(): Int {
        return R.layout.activity_location
    }
    override fun initViewModel(): LocationViewModel {
        return ViewModelProvider(this)[LocationViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun initView() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()

    }
    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_COARSE_LOCATION)
            !=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission is not Granted", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),100 )
        }

        //get latitude and longitude
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null){
                val textLatitude = " Latitude: " + it.latitude.toString()
                val textLongitude =  " Longitude: " + it.longitude.toString()
                dataBinding.latitudeTextview.text = textLatitude
                dataBinding.longitudeTextview.text = textLongitude
                Toast.makeText(this, "Location Works", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show()
            }}
    }


}