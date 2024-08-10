package com.example.koratime.stadiums.manageStadium

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import com.example.koratime.stadiums.booking_requests.BookingRequestsActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DefaultLocale", "SetTextI18n")
class ManageStadiumActivity : BasicActivity<ActivityManageStadiumBinding, ManageStadiumViewModel>(),
    ManageStadiumNavigator {
    override val TAG: String
        get() = "ManageStadiumActivity"
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

    override fun callback() {
        viewModel.apply {
            dataBinding.vm = viewModel
            navigator = this@ManageStadiumActivity
            stadiumModel = intent.getParcelableExtra(Constants.STADIUM_MANAGER)!!
            stadium = stadiumModel
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
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


        }


        calendarAdapter.onItemClickListener = object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(
                date: Date?,
                holder: CalendarAdapter.CalendarViewHolder,
                position: Int
            ) {
                calendarAdapter.changeDate(date!!)
                selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(date)
                log("Date selected: $selectedDate")
                selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
                dataBinding.dateTitle.text = selectedYear
                getTimeSlots(selectedDate)
            }
        }

        timeSlotsAdapter.onBookClickListener = object : TimeSlotsForManagerAdapter.OnBookClickListener {
            override fun onclick(slot: String, holder: TimeSlotsForManagerAdapter.ViewHolder, position: Int) {
                holder.dataBinding.apply {
                    tvTimeSlot.setOnLongClickListener {
                        removeBookingFromFirestore(
                            timeSlot = tvTimeSlot.text.toString(),
                            stadiumID = stadiumModel.stadiumID!!,
                            date = selectedDate,
                            onSuccessListener = {
                                log("Book has been Removed successfully from firestore")
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
                                log("Error Removing Book from firestore $it")
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
                                log(" ${holder.dataBinding.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} to the stadiumID: ${stadiumModel.stadiumID}")
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
                                log("Error Removing Book from firestore $e")
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
                log("Booked times $bookedTimesList")

                // create opening and closing list
                timeSlotsList = viewModel.createListForOpeningTimes(
                    stadiumModel.opening!!,
                    stadiumModel.closing!!,
                    resources.getStringArray(R.array.time_slots)
                )
                log("TimeSlots List: $timeSlotsList")

                availableSlots =
                    viewModel.removeBookedListFromOpeningTimes(timeSlotsList, bookedTimesList)
                log("AvailableSlots List: $availableSlots")

                timeSlotsAdapter.updateTimeSlots(timeSlotsList, bookedTimesList)

            },
            onFailureListener = { e ->
                log("Error fetching booked times $e")
            }
        )
    }

    private fun getStadiumImages() {
        getMultipleImagesFromFirestore(
            stadiumID = stadiumModel.stadiumID!!,
            onSuccessListener = { urls ->
                slideImageList.clear()
                slideImageList.addAll(urls)
                log("List of $urls")
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
                log("Failed To get Images From firestore $it")
            }
        )
    }

    private fun openImagePicker() {
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
                // photo picker
                if (uris.isNotEmpty()) {
                    viewModel.showLoading.value = true
                    dataBinding.imagePickerTextView.text = "Uploading Images.."
                    log("PhotoPicker: Selected URIs: $uris")
                    //upload images to storage
                    uploadMultipleImagesToStorage(uris = uris, stadiumID = stadiumModel.stadiumID!!,
                        onSuccessListener = { imagesList ->
                            log("Images uploaded successfully to storage")
                            viewModel.apply {
                                listOfUrls.value = imagesList
                                addImageUrlsToFirestore()
                            }
                            dataBinding.imagePickerTextView.text = "Images Selected"

                        },
                        onFailureListener = {
                            log("Error uploading images to storage $it")
                        }
                    )


                } else {
                    dataBinding.imagePickerTextView.text = " No Image Selected"
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                    log("PhotoPicker: No media selected")
                }


            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stadium_manager_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.booking_requests -> {
                val intent = Intent(this, BookingRequestsActivity::class.java)
                intent.putExtra(Constants.STADIUM, viewModel.stadium)
                startActivity(intent)
                return true
            }
            R.id.delete_stadium -> {
                deleteStadiumFromFirestore(
                    stadiumID = stadiumModel.stadiumID!!,
                    onSuccessListener = {
                        log("Stadium Removed Successfully from firestore")
                        finish()
                        },
                    onFailureListener = {
                        log("Error Removing Stadium from firestore")
                    }
                )
                return true

            }
            }

        return  super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

