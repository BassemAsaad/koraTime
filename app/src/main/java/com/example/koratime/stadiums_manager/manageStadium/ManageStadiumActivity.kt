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
import com.example.koratime.database.deleteStadiumFromFirestore
import com.example.koratime.database.getBookedTimesFromFirestore
import com.example.koratime.database.getMultipleImagesFromFirestore
import com.example.koratime.database.removeBookingFromFirestore
import com.example.koratime.database.uploadMultipleImagesToStorage
import com.example.koratime.databinding.ActivityManageStadiumBinding
import com.example.koratime.model.StadiumModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class ManageStadiumActivity : BasicActivity<ActivityManageStadiumBinding,ManageStadiumViewModel>(),ManageStadiumNavigator{
    private lateinit var stadiumModel : StadiumModel
    private  var adapter = TimeSlotsForManagerAdapter(emptyList(), emptyList())
    private lateinit var timeSlotsList :List<String>
    private lateinit var availableSlots: List<String>
    private lateinit var bookedTimesList : List<String>
    private lateinit var selectedDate: String
    val slideImageList = mutableListOf<String>()
    private lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>

    override fun getLayoutID(): Int {
        return R.layout.activity_manage_stadium
    }

    override fun initViewModel(): ManageStadiumViewModel {
        return ViewModelProvider(this)[ManageStadiumViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }
    @SuppressLint("SetTextI18n")
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
                holder.dataBinding.tvTimeSlot.setOnLongClickListener {
                    removeBookingFromFirestore(
                        timeSlot= holder.dataBinding.tvTimeSlot.text.toString(),
                        stadiumID = stadiumModel.stadiumID!!,
                        date = selectedDate,
                        onSuccessListener = {
                            Log.e("Firebase"," Book has been Removed successfully")
                            holder.dataBinding.tvTimeSlot.isEnabled=true
                            holder.dataBinding.tvTimeSlot.setTextColor((Color.BLACK))
                            holder.dataBinding.btnBook.isEnabled=true
                            holder.dataBinding.btnBook.text= "Book"
                            holder.dataBinding.btnBook.backgroundTintList = null
                            Toast.makeText(this@ManageStadiumActivity,"${holder.dataBinding.tvTimeSlot.text} Book Removed Successfully", Toast.LENGTH_SHORT).show()
                            getBookedTimes(selectedDate)
                        },
                        onFailureListener = {
                            Log.e("Firebase"," Book is not found")
                            getBookedTimes(selectedDate)

                        }

                    )
                    true
                }

                holder.dataBinding.btnBook.setOnClickListener {
                    addBookingToFirestore(
                        timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                        stadiumID = stadiumModel.stadiumID!!,
                        date = selectedDate, userId = DataUtils.user!!.id!!,
                        onSuccessListener = {

                            holder.dataBinding.tvTimeSlot.isEnabled=false
                            holder.dataBinding.tvTimeSlot.setTextColor((Color.GRAY))
                            holder.dataBinding.btnBook.isEnabled=false
                            holder.dataBinding.btnBook.text= "Booked"
                            holder.dataBinding.btnBook.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

                            Log.e("Firebase"," ${holder.dataBinding.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} to the stadiumID: ${stadiumModel.stadiumID}")
                            Toast.makeText(this@ManageStadiumActivity,"${holder.dataBinding.tvTimeSlot.text} Booked Successfully", Toast.LENGTH_SHORT).show()
                            getBookedTimes(selectedDate)

                        },
                        onFailureListener = {e->
                            Log.e("Firebase"," Error:  ${holder.dataBinding.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} ",e) }
                    )
                }


            }
        }

        openImagePicker()
        dataBinding.imagePicker.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        getMultipleImagesFromFirestore(
            stadiumID = stadiumModel.stadiumID!!,
            onSuccessListener = {urls->
                dataBinding.imagePickerTextView.text = "Images Selected"
                slideImageList.clear()
                slideImageList.addAll(urls)
                Log.e("Firebase"," List of $urls")
                val imageList = ArrayList<SlideModel>()
                for ( i in slideImageList ){
                    imageList.add(SlideModel(i, ""))
                }

                if (imageList.isNotEmpty()){
                    dataBinding.stadiumImages.visibility =View.VISIBLE
                    dataBinding.imageSlider.visibility =View.VISIBLE
                    dataBinding.imageSlider.setImageList(imageList,ScaleTypes.FIT)
                }

            },
            onFailureListener = {
                Log.e("Firebase","Failed To get Images From firestore")
            }
        )
        dataBinding.swipeRefresh.setOnRefreshListener {
            dataBinding.swipeRefresh.isRefreshing = false
            getMultipleImagesFromFirestore(
                stadiumID = stadiumModel.stadiumID!!,
                onSuccessListener = {urls->
                    slideImageList.clear()
                    slideImageList.addAll(urls)
                    Log.e("Firebase"," List of $urls")
                    val imageList = ArrayList<SlideModel>()
                    for ( i in slideImageList ){
                        imageList.add(SlideModel(i, ""))
                    }

                    if (imageList.isNotEmpty()){
                        dataBinding.stadiumImages.visibility = View.VISIBLE
                        dataBinding.imageSlider.visibility = View.VISIBLE
                        dataBinding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
                    }

                },
                onFailureListener = {
                    Log.e("Firebase","Failed To get Images From firestore")
                }
            )
        }


        dataBinding.deleteStadiun.setOnClickListener {
            deleteStadiumFromFirestore(
                stadiumID = stadiumModel.stadiumID!!,
                onSuccessListener = {
                    Log.e("Firebase "," Stadium Removed Successfully from firestore")
                },
                onFailureListener = {
                    Log.e("Firebase ","Error Removing Stadium from firestore")

                }
            )

        }
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
    @SuppressLint("SetTextI18n")
    private fun openImagePicker(){
        Log.e("StadiumID","${stadiumModel.stadiumID}")
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
            // photo picker
            if (uris.isNotEmpty()) {
                dataBinding.imagePickerTextView.text = "Uploading Images.."
                Log.e("PhotoPicker", "Selected URI: $uris")
                viewModel.showLoading.value=true
                //upload images to storage
                uploadMultipleImagesToStorage(uris = uris, stadiumID = stadiumModel.stadiumID!!,
                    onSuccessListener = {imagesList->
                        Log.e("Firebase","Images uploaded successfully to storage")
                        viewModel.listOfUrls.value = imagesList
                        viewModel.addImageUrlsToFirestore()
                        dataBinding.imagePickerTextView.text = "Images Selected"

                },
                    onFailureListener = {
                        Log.e("Firebase","Error uploading images to storage")
                    }
                )


            } else {
                dataBinding.imagePickerTextView.text = " No Image Selected"
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                Log.e("PhotoPicker", "No image selected")
            }


        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }
}

