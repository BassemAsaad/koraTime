package com.example.koratime.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityLocationPickerBinding
import com.example.koratime.model.LocationModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

@Suppress("DEPRECATION")
class LocationPickerActivity :
    BasicActivity<ActivityLocationPickerBinding, LocationPickerViewModel>(), OnMapReadyCallback {
    override val TAG: String
        get() = "LocationPickerActivity"
    companion object {
        private const val DEFAULT_ZOOM = 13.8
    }

    private var mMap: GoogleMap? = null
    private var placeClient: PlacesClient? = null
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private var myLastKnownLocation: Location? = null
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null
    private var address = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_location_picker
    }

    override fun initViewModel(): LocationPickerViewModel {
        return ViewModelProvider(this)[LocationPickerViewModel::class.java]
    }


    override fun initView() {
        setSupportActionBar(dataBinding.toolbar)
        callback()

    }
    override fun callback() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        dataBinding.apply {
            container.visibility = View.GONE
            doneButton.setOnClickListener {
                val locationModel = LocationModel(
                    latitude = selectedLatitude,
                    longitude = selectedLongitude,
                    address = address
                )
                val intent = Intent()
                intent.putExtra(Constants.LOCATION, locationModel)
                setResult(Activity.RESULT_OK, intent)
                finish()

            }
            // get map
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
            mapFragment.getMapAsync(this@LocationPickerActivity)

            // initialize places client
            Places.initialize(this@LocationPickerActivity, getString(R.string.google_api_key))

            // create new places client instance
            placeClient = Places.createClient(this@LocationPickerActivity)
            fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this@LocationPickerActivity)
        }
    }

    private fun addMarker(latLng: LatLng, title: String, address: String) {
        //we only need one location marker so if there is one clear it
        mMap!!.clear()
        try {

            //setup marker options with latLng,title and full address
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title(" $title ")
            markerOptions.snippet(" $address ")
            markerOptions.icon(
                BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )

            // add marker to map and move camera to the new added marker
            mMap!!.addMarker(markerOptions)
            mMap!!.moveCamera(
                CameraUpdateFactory
                    .newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat())
            )
            //show doneL1 so user can go back with selected location
            dataBinding.container.visibility = View.VISIBLE

            //set complete address
            dataBinding.selectedPlace.text = address


        } catch (e: Exception) {
            log("addMarker: $e")
        }


    }

    // get location of device and position the map's camera
    @SuppressLint("MissingPermission")
    private fun detectAndShowDeviceLocationMap() {
        fusedLocationProvider!!.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    //save location
                    myLastKnownLocation = location
                    //get latitude and longitude
                    selectedLatitude = location.latitude
                    selectedLongitude = location.longitude

                    // setUp latLng
                    val latLng = LatLng(selectedLatitude!!, selectedLongitude!!)
                    mMap!!.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat())
                    )
                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM.toFloat()))

                    // function to get address details from latLng
                    addressFromLatLng(latLng)
                }
            }.addOnFailureListener { e ->
                log("detectAndShowDeviceLocationMap: $e")
            }
    }


    @SuppressLint("MissingPermission")
    private val requestLocationPermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            //check if permission is granted
            if (isGranted) {
                log("requestLocationPermission IsGranted: $isGranted")
                // enable gps button to set location on map
                mMap!!.isMyLocationEnabled = true
                pickCurrentPlace()

            }

        }

    private fun pickCurrentPlace() {
        if (mMap == null) {
            return
        }
        detectAndShowDeviceLocationMap()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //ask user for permission
        requestLocationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)

        // get latitude and longitude when ever user click on map
        mMap!!.setOnMapClickListener { latLng ->
            selectedLatitude = latLng.latitude
            selectedLongitude = latLng.longitude

            // function to get address details from latLng
            addressFromLatLng(latLng)
        }

    }

    private fun addressFromLatLng(latLng: LatLng) {

        val geoCoder = Geocoder(this)
        try {
            val addressList = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val firstAddress = addressList!![0]

            val addressLine = firstAddress.getAddressLine(0)
            val subLocality = firstAddress.subLocality

            address = " $addressLine"
            addMarker(latLng, " $subLocality ", " $addressLine ")


        } catch (e: Exception) {
            log("addressFromLatLng: $e")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }
}