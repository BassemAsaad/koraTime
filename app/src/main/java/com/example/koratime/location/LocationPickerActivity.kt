package com.example.koratime.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityLocationPickerBinding
import com.google.android.gms.common.api.Status
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
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.lang.Exception

class LocationPickerActivity : BasicActivity<ActivityLocationPickerBinding,LocationPickerViewModel>(),OnMapReadyCallback{



    companion object{
        private const val TAG="LOCATION_PICKER_TAG"
       private const val DEFAULT_ZOOM=15
    }
    private var mMap : GoogleMap?=null
    private var mPlace : PlacesClient?=null
    private var mFusedLocationProvider :FusedLocationProviderClient?=null
    private var mLastKnownLocation : Location?=null
    private var selectedLatitude :Double?=null
    private var selectedLongitude:Double?= null
    private var selectedAddress =""



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
        // Enable back button on Toolbar
        setSupportActionBar(dataBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)



        dataBinding.doneL1.visibility= View.GONE
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // initialize places client
        Places.initialize(this,getString(R.string.google_api_key))

        // create new places client instance
        mPlace= Places.createClient(this)
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)


        // initialize autoCompleteSupportFragmentManager to search places on map
        val autoCompleteSupportFragmentManager = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment )
                as AutocompleteSupportFragment

        // list of location fields we need in search result
        val placesList = arrayOf(Place.Field.ID,Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG)

        // set placesList to autoCompleteSupportFragmentManager
        autoCompleteSupportFragmentManager.setPlaceFields(listOf(*placesList))

        // listener for place selections
        autoCompleteSupportFragmentManager.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onPlaceSelected(place: Place) {
                Log.e(TAG,"onPlaceSelected")
                val id = place.id
                val name = place.name
                val latLng = place.latLng
                selectedLatitude = latLng?.latitude
                selectedLongitude = latLng?.longitude
                selectedAddress = place.address?:""
                Log.e(TAG,"onPlaceSelected id: $id")
                Log.e(TAG,"onPlaceSelected name: $name")
                Log.e(TAG,"onPlaceSelected selectedLatitude: $selectedLatitude")
                Log.e(TAG,"onPlaceSelected selectedLongitude: $selectedLongitude")
                Log.e(TAG,"onPlaceSelected selectedAddress: $selectedAddress")

                addMarker(latLng!!,name,selectedAddress)
            }

            override fun onError(status: Status) {
                TODO("Not yet implemented")
            }

        })


        dataBinding.gpsButton.setOnClickListener {
            if ( isGpsEnabled() ){
                //location enabled
                requestLocationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION  )

            } else{
                Toast.makeText(this, "Location is not enabled !! Turn it on to show current Location",
                    Toast.LENGTH_SHORT).show()
            }
        }


        // done Button click
        dataBinding.doneButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("latitude",selectedLatitude)
            intent.putExtra("longitude",selectedLongitude)
            intent.putExtra("address",selectedAddress)
            setResult(Activity.RESULT_OK,intent)
            finish()

        }


    }

    private fun addMarker(latLng: LatLng, title: String, address: String) {
        Log.e(TAG,"addMarker: latitude: ${latLng.latitude}")
        Log.e(TAG,"addMarker: longitude: ${latLng.longitude}")
        Log.e(TAG,"addMarker: title: $title")
        Log.e(TAG,"addMarker: address: $address")

        //we only need one location marker so if there is one clear it
        mMap!!.clear()
        try {

            //setup marker options with latLng,title and full address
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("$title")
            markerOptions.snippet("$address")
            markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

            // add marker to map and move camera to the new added marker
            mMap!!.addMarker(markerOptions)
            mMap!!.moveCamera(CameraUpdateFactory
                .newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat())
            )
            //show doneL1 so user can go back with selected location
            dataBinding.doneL1.visibility = View.VISIBLE

            //set complete address
            dataBinding.selectedPlace.text = address


        }catch (e : Exception){
            Log.e(TAG,"addMarker: ",e)
        }


    }

    // get location of device and position the map's camera
    @SuppressLint("MissingPermission")
    private fun detectAndShowDeviceLocationMap(){

        try {
            val locationResult =mFusedLocationProvider!!.lastLocation

            locationResult.addOnSuccessListener {location->
                if (location!=null){
                    //save location
                    mLastKnownLocation=location
                    //get latitude and longitude
                    selectedLatitude= location.latitude
                    selectedLongitude = location.longitude

                    Log.e(TAG,"detectAndShowDeviceLocationMap: selectedLatitude: $selectedLatitude")
                    Log.e(TAG,"detectAndShowDeviceLocationMap: selectedLongitude: $selectedLongitude")

                    // setUp latLng
                    val latLng = LatLng(selectedLatitude!!,selectedLongitude!!)
                    mMap!!.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat())
                    )

                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM.toFloat()))

                    // function to get address details from latLng
                    addressFromLatLng(latLng)

                }

            }
                .addOnFailureListener {e->
                    Log.e(TAG,"detectAndShowDeviceLocationMap: ",e)

                }

        }catch (e : Exception){
            Log.e(TAG,"detectAndShowDeviceLocationMap: ",e)

        }

    }


    private fun isGpsEnabled(): Boolean {

        val lm = getSystemService(Context.LOCATION_SERVICE) as  LocationManager
        var gpsEnabled = false
        var networkEnabled = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e : Exception){
            Log.e(TAG,"isGpsEnabled: ",e)
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e : Exception){
            Log.e(TAG,"isNetworkEnabled: ",e)
        }

        return  !(!gpsEnabled && !networkEnabled)

    }


    @SuppressLint("MissingPermission")
    private val requestLocationPermission : ActivityResultLauncher<String> =
        registerForActivityResult( ActivityResultContracts.RequestPermission() ){isGranted->
            Log.e( TAG,"requestLocationPermission IsGranted: $isGranted")

            //check if permission is granted
            if (isGranted){

                // enable gps button to set location on map
                mMap!!.isMyLocationEnabled= true
                pickCurrentPlace()


            }

        }

    private fun pickCurrentPlace() {
        Log.e(TAG,"pickCurrentPlace: ")
        if (mMap == null){
            return
        }
        detectAndShowDeviceLocationMap()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.e(TAG,"onMapReady")
        mMap = googleMap

        //ask user for permission
        requestLocationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)

        // get latitude and longitude when ever user click on map
        mMap!!.setOnMapClickListener {latLng->
            selectedLatitude=latLng.latitude
            selectedLongitude=latLng.longitude

            Log.e(TAG,"onMapReady selectedLatitude: $selectedLatitude")
            Log.e(TAG,"onMapReady selectedLongitude: $selectedLongitude")


            // function to get address details from latLng
            addressFromLatLng(latLng)



        }


    }

    private fun addressFromLatLng(latLng: LatLng) {


        val geoCoder = Geocoder(this)
        try {
            val addressList = geoCoder.getFromLocation(latLng.latitude,latLng.longitude,1)
            val address =addressList!![0]

            val addressLine=address.getAddressLine(0)
            val subLocality = address.subLocality

            selectedAddress = "$addressLine"
            addMarker(latLng,"$subLocality","$addressLine")


        } catch (e : Exception){
            Log.e(TAG,"address from latLng: ",e)

        }

    }



    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }
}