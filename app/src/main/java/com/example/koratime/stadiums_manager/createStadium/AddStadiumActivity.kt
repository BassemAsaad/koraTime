package com.example.koratime.stadiums_manager.createStadium

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.uploadImageToStorage
import com.example.koratime.databinding.ActivityAddStadiumBinding
import com.example.koratime.location.LocationPickerActivity

@Suppress("DEPRECATION")
class AddStadiumActivity : BasicActivity<ActivityAddStadiumBinding,AddStadiumViewModel>(), AddStadiumNavigator {

    private lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>
    private var latitude = 0.0
    private var longitude = 0.0
    private var address = ""
    private var openingTimeIndex:Int?=null
    private var closingTimeIndex:Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_add_stadium
    }

    override fun initViewModel(): AddStadiumViewModel {
        return ViewModelProvider(this)[AddStadiumViewModel::class.java]
    }

    override fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator=this
        setSupportActionBar(dataBinding.toolbar)
        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setupSpinners()

        openImagePicker()
        dataBinding.stadiumImagesLayout.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        dataBinding.locationPickerEditText.setOnClickListener {
            val intent = Intent(this,LocationPickerActivity::class.java)
            locationPickerActivityResultLauncher.launch(intent)
        }

        viewModel.toastMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }


    private val locationPickerActivityResultLauncher =
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()){result->
            //get result from location picked from location picker activity
            if (result.resultCode== Activity.RESULT_OK){
                //get data Intent from result param
                val data = result.data
                // if data not null assign data
                if ( data !=null ){
                    latitude = data.getDoubleExtra("latitude",0.0)
                    longitude = data.getDoubleExtra("longitude",0.0)
                    address = data.getStringExtra("address")?:""
                    viewModel.latitudeLiveData.value = latitude
                    viewModel.longitudeLiveData.value=longitude
                    viewModel.addressLiveData.value = address
                    dataBinding.locationPickerEditText.setText(address)
                }
            }else{
                Log.e("Add Stadium","locationPickerActivityResultLauncher: cancelled ")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }


    private fun setupSpinners() {
        val timeSlots = resources.getStringArray(R.array.time_slots)

        val adapterSlots = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSlots)
        adapterSlots.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        dataBinding.opening.adapter = adapterSlots
        dataBinding.closing.adapter = adapterSlots

        // Add listeners to update closing time spinner based on opening time spinner selection
        dataBinding.opening.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Update opening time index
                openingTimeIndex = position
                viewModel.openingTime.value = openingTimeIndex
                updateClosingTimeSpinner(dataBinding.opening, dataBinding.closing)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Add listener for closing time spinner
        dataBinding.closing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Update closing time index
                closingTimeIndex = position
                viewModel.closingTime.value = closingTimeIndex!! + 1 // Add +1 to represent the actual closing time
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    // Update closing time spinner based on opening time spinner
    private fun updateClosingTimeSpinner(openingSpinner: Spinner, closingSpinner: Spinner) {
        val openingTime = openingSpinner.selectedItem.toString()
        val timeSlots= resources.getStringArray(R.array.time_slots)

        // Generate closing time slots starting from one hour after the opening time
        val closingTimeSlots = timeSlots.sliceArray((openingTimeIndex!! + 1) until timeSlots.size).toMutableList()

        val closingAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, closingTimeSlots)
        closingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        closingSpinner.adapter = closingAdapter

        // Adjust selection if closing time is at the end of time slots array
        val selectedClosingTimeIndex = if (closingTimeSlots.contains(openingTime)) {
            closingTimeSlots.indexOf(openingTime) + 1
        } else {
            0
        }
        closingSpinner.setSelection(selectedClosingTimeIndex)

        // Update closing time index
        closingTimeIndex = selectedClosingTimeIndex
    }


    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

    private fun openImagePicker(){
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // photo picker
            if (uri != null) {
                viewModel.showLoading.value =true
                Log.d("PhotoPicker", "Selected URI: $uri")
                uploadImageToStorage(uri,
                    onSuccessListener = { downloadUri ->
                        Log.e("Firebase Storage:", "Image uploaded successfully")
                        // pass imageUrl to view model
                        viewModel.imageUrl.value = downloadUri.toString()
                        viewModel.showLoading.value = false
                        dataBinding.stadiumImagesLayout.setImageURI(uri)
                        dataBinding.stadiumImagesTextLayout.text = "Change Picture Chosen"
                    },
                    onFailureListener = {
                        Log.e("Firebase Storage:", it.localizedMessage!!.toString())
                        viewModel.showLoading.value = false

                    }
                )


            } else {
                dataBinding.stadiumImagesTextLayout.text = "Default Picture"
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                Log.d("PhotoPicker", "No image selected")
                viewModel.showLoading.value = false
            }
        }
    }

    override fun closeActivity() {
        Toast.makeText(this, "Stadium Created Successfully", Toast.LENGTH_SHORT).show()
        finish()
    }


}