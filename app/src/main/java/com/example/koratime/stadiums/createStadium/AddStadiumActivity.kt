package com.example.koratime.stadiums.createStadium

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.uploadImageToStorage
import com.example.koratime.databinding.ActivityAddStadiumBinding
import com.example.koratime.location.LocationPickerActivity
import com.example.koratime.model.LocationModel

@Suppress("DEPRECATION","SetTextI18n")
class AddStadiumActivity : BasicActivity<ActivityAddStadiumBinding, AddStadiumViewModel>(),
    AddStadiumNavigator {
    override val TAG: String
        get() = "AddStadiumActivity"

    private lateinit var locationModel :LocationModel
    private var openingTimeIndex: Int? = null
    private var closingTimeIndex: Int? = null
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // photo picker
        if (uri != null) {
            log("PhotoPicker: Selected URI: $uri")
            viewModel.imagesUri.value = uri
            dataBinding.apply {
                stadiumImagesLayout.setImageURI(uri)
                stadiumImagesTextLayout.text = "Change Picture Chosen"
            }

        } else {
            viewModel.showLoading.value = false
            dataBinding.stadiumImagesTextLayout.text = "Default Picture"
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            log("PhotoPicker: No media selected")
        }
    }
    private val locationPickerActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //get result from location picked from location picker activity
            if (result.resultCode == Activity.RESULT_OK) {
                //get data Intent from result param
                val data = result.data
                // if data not null assign data
                if (data != null) {
                    locationModel = data.getParcelableExtra(Constants.LOCATION)!!
                    viewModel.apply {
                        latitudeLiveData.value = locationModel.latitude
                        longitudeLiveData.value = locationModel.longitude
                        addressLiveData.value = locationModel.address
                    }

                    dataBinding.locationPickerEditText.setText(locationModel.address)
                }
            } else {
                Log.e("Add Stadium", "locationPickerActivityResultLauncher: cancelled ")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    override fun getLayoutID(): Int {
        return R.layout.activity_add_stadium
    }

    override fun initViewModel(): AddStadiumViewModel {
        return ViewModelProvider(this)[AddStadiumViewModel::class.java]
    }

    override fun initView() {
        setSupportActionBar(dataBinding.toolbar)
        callback()
        setupSpinners()

    }

    override fun callback() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        viewModel.apply {
            navigator = this@AddStadiumActivity
            toastMessage.observe(this@AddStadiumActivity, Observer { message ->
                Toast.makeText(this@AddStadiumActivity, message, Toast.LENGTH_SHORT).show()
            })
        }

        dataBinding.apply {
            vm = viewModel
            stadiumImagesLayout.setOnClickListener {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            locationPickerEditText.setOnClickListener {
                val intent = Intent(this@AddStadiumActivity, LocationPickerActivity::class.java)
                locationPickerActivityResultLauncher.launch(intent)
            }
        }
    }



    private fun setupSpinners() {
        val timeSlots = resources.getStringArray(R.array.time_slots)
        val adapterSlots = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSlots)
        adapterSlots.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        dataBinding.apply {
            opening.adapter = adapterSlots
            closing.adapter = adapterSlots
            // Add listeners to update closing time spinner based on opening time spinner selection
            opening.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Update opening time index
                    openingTimeIndex = position
                    viewModel.openingTime.value = openingTimeIndex
                    updateClosingTimeSpinner(opening, closing)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }

            // Add listener for closing time spinner
            closing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Update closing time index
                    closingTimeIndex = position
                    // Add +1 to represent the actual closing time
                    viewModel.closingTime.value = closingTimeIndex!! + 1
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }
        }
    }

    private fun updateClosingTimeSpinner(openingSpinner: Spinner, closingSpinner: Spinner) {
        // Update closing time spinner based on opening time spinner

        val openingTime = openingSpinner.selectedItem.toString()
        val timeSlots = resources.getStringArray(R.array.time_slots)

        // Generate closing time slots starting from one hour after the opening time
        val closingTimeSlots =
            timeSlots.sliceArray((openingTimeIndex!! + 1) until timeSlots.size).toMutableList()

        val closingAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, closingTimeSlots)
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

    override fun closeActivity() {
        Toast.makeText(this, "Stadium Created Successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

}