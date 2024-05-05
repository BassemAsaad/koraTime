package com.example.koratime.stadiums_manager.manageStadium

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.TimeSlotsForManagerAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.addBookingToFirestore
import com.example.koratime.database.getBookedTimesFromFirestore
import com.example.koratime.database.getMultipleImageFromFirestore
import com.example.koratime.database.uploadMultipleImageToFirestore
import com.example.koratime.database.uploadMultipleImages
import com.example.koratime.databinding.ActivityManagingStadiumBinding
import com.example.koratime.model.StadiumModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class ManagingStadiumActivity : BasicActivity<ActivityManagingStadiumBinding,ManagingStadiumViewModel>(),ManagingStadiumNavigator{
    private lateinit var stadiumModel : StadiumModel
    private  var adapter = TimeSlotsForManagerAdapter(emptyList(), emptyList())
    private lateinit var timeSlotsList :List<String>
    private lateinit var availableSlots: List<String>
    private lateinit var bookedTimesList : List<String>
    private lateinit var selectedDate: String
    private  var imgsListUrl= mutableListOf <String>()

    private lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>

    override fun getLayoutID(): Int {
        return R.layout.activity_managing_stadium
    }

    override fun initViewModel(): ManagingStadiumViewModel {
        return ViewModelProvider(this)[ManagingStadiumViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }
    override fun initView() {
        viewModel.navigator=this
        dataBinding.vm = viewModel
        dataBinding.imageSlider.visibility =View.GONE
        dataBinding.stadiumImages.visibility =View.GONE

        dataBinding.calendarView.minDate = System.currentTimeMillis()
        dataBinding.calendarView.startAnimation(AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_slide_out_top))

        stadiumModel = intent.getParcelableExtra(Constants.STADIUM_USER)!!
        viewModel.stadium = stadiumModel


        setSupportActionBar(dataBinding.toolbar)
        // Enable back button on Toolbar and title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Enable title on Toolbar
        supportActionBar?.title = stadiumModel.stadiumName + " Stadium"
        supportActionBar?.setDisplayShowTitleEnabled(true)

        //select date
        selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(Date())

        getBookedTimes(selectedDate)

        // Add an OnDateChangeListener to the CalendarView
        dataBinding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate = String.format("%02d_%02d_%04d", month + 1, dayOfMonth, year)
            Log.e("Firebase"," Date selected: $selectedDate  ")
            getBookedTimes(selectedDate)

        }

        // Set up click listener for booking button in the adapter
        adapter.onBookClickListener = object : TimeSlotsForManagerAdapter.OnBookClickListener {
            @SuppressLint("SetTextI18n")
            override fun onclick(slot: String, holder: TimeSlotsForManagerAdapter.ViewHolder, position: Int) {
                addBookingToFirestore(timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                    stadiumID = stadiumModel.stadiumID!!,
                    date = selectedDate, userId = DataUtils.user!!.id!!,
                    onSuccessListener = {

                        holder.dataBinding.tvTimeSlot.isEnabled=false
                        holder.dataBinding.tvTimeSlot.setTextColor((Color.GRAY))
                        holder.dataBinding.btnBook.isEnabled=false
                        holder.dataBinding.btnBook.text= "Booked"
                        holder.dataBinding.btnBook.backgroundTintList = ColorStateList.valueOf(Color.GRAY)


                        Log.e("Firebase"," ${holder.dataBinding.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} to the stadiumID: ${stadiumModel.stadiumID}")
                        Toast.makeText(this@ManagingStadiumActivity,"${holder.dataBinding.tvTimeSlot.text} Booked Successfully", Toast.LENGTH_SHORT).show()

                        getBookedTimes(selectedDate)

                    },
                    onFailureListener = {e->
                        Log.e("Firebase"," Error:  ${holder.dataBinding.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} ",e) }
                )

            }
        }

        openImagePicker()
        dataBinding.imagePicker.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        var r = mutableListOf<String>()
        getMultipleImageFromFirestore(
            stadiumID = stadiumModel.stadiumID!!,
            onSuccessListener = {urls->
                r.clear()
                r.addAll(urls)
                Log.e("Firebase"," List of $urls")
                val imageList = ArrayList<SlideModel>()
                for ( i in r ){
                    imageList.add(SlideModel(i, ""))
                }

                if (imageList.isNotEmpty()){
                    dataBinding.stadiumImages.visibility =View.VISIBLE
                    dataBinding.imageSlider.visibility =View.VISIBLE
                    dataBinding.imageSlider.setImageList(imageList,ScaleTypes.FIT)
                }

            },
            onFailureListener = {}
        )



    }

    private fun getBookedTimes(date: String) {
        getBookedTimesFromFirestore(
            stadiumID = stadiumModel.stadiumID!!,
            date = date,
            onSuccessListener = { bookedList ->

                bookedTimesList = bookedList
                Log.e("BookedSlots List","$bookedTimesList")

                // create opening and closing list
                timeSlotsList = viewModel.createListForOpeningTimes(stadiumModel.opening!!,stadiumModel.closing!!,resources.getStringArray(R.array.time_slots))
                Log.e("TimeSlots List","$timeSlotsList")

                availableSlots= viewModel.removeBookedListFromOpeningTimes(timeSlotsList,bookedTimesList)
                Log.e("AvailableSlots List","$availableSlots")

                adapter.updateTimeSlots(timeSlotsList,bookedTimesList)
                dataBinding.recyclerView.adapter=adapter
            },
            onFailureListener = { e->
                Log.e("Firebase", "Error fetching booked times", e)
            }
        )
    }
    private fun openImagePicker(){
        Log.e("StadiumID","${stadiumModel.stadiumID}")
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
            // photo picker
            if (uris != null) {
                viewModel.showLoading.value=true
                Log.e("PhotoPicker", "Selected URI: $uris")
                uploadMultipleImages(uris = uris, stadiumID = stadiumModel.stadiumID!!,
                    onSuccessListener = {imagesList->
                        Log.e("Firebase","Images uploaded successfully to storage")
                        imgsListUrl.addAll(imagesList)
                        uploadMultipleImageToFirestore(
                            imgsListUrl,stadiumModel.stadiumID!!,
                            onSuccessListener = {
                                Log.e("Firebase","Images uploaded successfully to firestore")
                                viewModel.showLoading.value=false
                            },
                            onFailureListener = {
                                viewModel.showLoading.value=false

                                Log.e("Firebase","Error uploading images to firestore")

                            }
                        )
                },
                    onFailureListener = {
                        viewModel.showLoading.value=false
                        Log.e("Firebase","Error uploading images to storage")
                    }
                )


            } else {
                viewModel.showLoading.value=false
                dataBinding.imagePickerTextView.text = " No Image Selected"
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                Log.e("PhotoPicker", "No image selected")
            }
            viewModel.showLoading.value=false


        }
    }
    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }
}

