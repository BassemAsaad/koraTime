package com.example.koratime.stadiums_manager.manageStadium

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.View
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
import com.example.koratime.adapters.CalendarAdapter
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
import com.example.koratime.stadiums_manager.manageStadium.booking_requests.BookingRequestsActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DefaultLocale", "SetTextI18n")
class ManageStadiumActivity : BasicActivity<ActivityManageStadiumBinding, ManageStadiumViewModel>(),
    ManageStadiumNavigator {
    private lateinit var stadiumModel: StadiumModel

    private var timeSlotsAdapter = TimeSlotsForManagerAdapter(emptyList(), emptyList())
    private var calendarAdapter = CalendarAdapter(emptyList())

    private lateinit var timeSlotsList: List<String>
    private lateinit var availableSlots: List<String>
    private lateinit var bookedTimesList: List<String>

    private var selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(Date())
    private var selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

    private val slideImageList = mutableListOf<String>()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    companion object {
        const val TAG = "ManageStadiumActivity"
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_manage_stadium
    }

    override fun initViewModel(): ManageStadiumViewModel {
        return ViewModelProvider(this)[ManageStadiumViewModel::class.java]
    }

    override fun initView() {
        setSupportActionBar(dataBinding.toolbar)
        callback()
        getStadiumImages()
    }

    fun callback() {
        viewModel.apply {
            dataBinding.vm = viewModel
            navigator = this@ManageStadiumActivity
            stadiumModel = intent.getParcelableExtra(Constants.STADIUM_MANAGER)!!
            stadium = stadiumModel
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = stadiumModel.stadiumName
        }



        dataBinding.apply {
            calendarAdapter = CalendarAdapter(viewModel.generateNextTwoWeeks())
            calendarRecyclerView.adapter = calendarAdapter
            getTimeSlots(selectedDate)
            slotsRecyclerView.adapter = timeSlotsAdapter

            dateTitle.text = selectedYear
            swipeRefresh.setOnRefreshListener {
                dataBinding.swipeRefresh.isRefreshing = false
                getStadiumImages()
            }
            openImagePicker()
            imagePicker.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            deleteStadiun.setOnClickListener {
                deleteStadiumFromFirestore(
                    stadiumID = stadiumModel.stadiumID!!,
                    onSuccessListener = {
                        Log.e(TAG, " Stadium Removed Successfully from firestore")
                        finish()

                    },
                    onFailureListener = {
                        Log.e(TAG, "Error Removing Stadium from firestore")
                    }
                )

            }
            notificationIc.setOnClickListener {
                val intent = Intent(this@ManageStadiumActivity, BookingRequestsActivity::class.java)
                intent.putExtra(Constants.STADIUM, viewModel.stadium)
                startActivity(intent)
            }

        }


        calendarAdapter.onItemClickListener = object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(
                date: Date?,
                holder: CalendarAdapter.CalendarViewHolder,
                position: Int
            ) {
                calendarAdapter.changeDate(date!!)
                selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(date)
                Log.e(TAG, "Date selected: $selectedDate")
                selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
                dataBinding.dateTitle.text = selectedYear
                getTimeSlots(selectedDate)
            }
        }
        // Set up click listener for booking button in the adapter
        timeSlotsAdapter.onBookClickListener = object : TimeSlotsForManagerAdapter.OnBookClickListener {
            override fun onclick(slot: String, holder: TimeSlotsForManagerAdapter.ViewHolder, position: Int) {
                holder.dataBinding.apply {
                    tvTimeSlot.setOnLongClickListener {
                        removeBookingFromFirestore(
                            timeSlot = tvTimeSlot.text.toString(),
                            stadiumID = stadiumModel.stadiumID!!,
                            date = selectedDate,
                            onSuccessListener = {
                                Log.e("Firebase", " Book has been Removed successfully")
                                holder.dataBinding.apply {
                                    tvTimeSlot.apply {
                                        isEnabled = true
                                        setTextColor((Color.BLACK))
                                    }
                                    btnBook.apply {
                                        isEnabled = true
                                        text = "Book"
                                        backgroundTintList = null
                                    }
                                }
                                Toast.makeText(
                                    this@ManageStadiumActivity,
                                    "${holder.dataBinding.tvTimeSlot.text} Book Removed Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                getTimeSlots(selectedDate)
                            },
                            onFailureListener = {
                                Log.e("Firebase", "Error: ", it)
                            }

                        )
                        true
                    }
                    btnBook.setOnClickListener {
                        addBookingToFirestore(
                            timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                            stadiumID = stadiumModel.stadiumID!!,
                            date = selectedDate,
                            user = DataUtils.user!!,
                            onSuccessListener = {
                                Log.e(
                                    "Firebase",
                                    " ${holder.dataBinding.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} to the stadiumID: ${stadiumModel.stadiumID}"
                                )

                                holder.dataBinding.apply {
                                    tvTimeSlot.apply {
                                        isEnabled = false
                                        setTextColor((Color.GRAY))

                                    }
                                    btnBook.apply {
                                        isEnabled = false
                                        text = "Booked"
                                        backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                                    }
                                }

                                Toast.makeText(this@ManageStadiumActivity,
                                    "${holder.dataBinding.tvTimeSlot.text} Booked Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                getTimeSlots(selectedDate)

                            },
                            onFailureListener = { e ->
                                Log.e("Firebase", " Error:  ${holder.dataBinding.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} ", e)
                            }
                        )
                    }
                }

            }
        }
    }

    private fun getTimeSlots(date: String) {
        getBookedTimesFromFirestore(
            stadiumID = stadiumModel.stadiumID!!,
            date = date,
            onSuccessListener = { bookedList ->

                bookedTimesList = bookedList
                Log.e(TAG, "BookedSlots List: $bookedTimesList")

                // create opening and closing list
                timeSlotsList = viewModel.createListForOpeningTimes(
                    stadiumModel.opening!!,
                    stadiumModel.closing!!,
                    resources.getStringArray(R.array.time_slots)
                )
                Log.e(TAG, "TimeSlots List: $timeSlotsList")

                availableSlots =
                    viewModel.removeBookedListFromOpeningTimes(timeSlotsList, bookedTimesList)
                Log.e(TAG, "AvailableSlots List: $availableSlots")

                timeSlotsAdapter.updateTimeSlots(timeSlotsList, bookedTimesList)

            },
            onFailureListener = { e ->
                Log.e(TAG, "Error fetching booked times", e)
            }
        )
    }

    private fun getStadiumImages() {
        getMultipleImagesFromFirestore(
            stadiumID = stadiumModel.stadiumID!!,
            onSuccessListener = { urls ->
                slideImageList.clear()
                slideImageList.addAll(urls)
                Log.e(TAG, " List of $urls")
                val imageList = ArrayList<SlideModel>()
                for (i in slideImageList) {
                    imageList.add(SlideModel(i, ""))
                }

                if (imageList.isNotEmpty()) {
                    dataBinding.stadiumImages.visibility = View.VISIBLE
                    dataBinding.imageSlider.visibility = View.VISIBLE
                    dataBinding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
                }

            },
            onFailureListener = {
                Log.e(TAG, "Failed To get Images From firestore")
            }
        )
    }

    private fun openImagePicker() {
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
                // photo picker
                if (uris.isNotEmpty()) {
                    dataBinding.imagePickerTextView.text = "Uploading Images.."
                    Log.e(TAG, "PhotoPicker: Selected URI: $uris")
                    viewModel.showLoading.value = true
                    //upload images to storage
                    uploadMultipleImagesToStorage(uris = uris, stadiumID = stadiumModel.stadiumID!!,
                        onSuccessListener = { imagesList ->
                            Log.e(TAG, "Images uploaded successfully to storage")
                            viewModel.apply {
                                listOfUrls.value = imagesList
                                addImageUrlsToFirestore()
                            }
                            dataBinding.imagePickerTextView.text = "Images Selected"

                        },
                        onFailureListener = {
                            Log.e(TAG, "Error uploading images to storage")
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
        // go to the previous fragment when back button clicked
        onBackPressed()
        return true
    }

}

