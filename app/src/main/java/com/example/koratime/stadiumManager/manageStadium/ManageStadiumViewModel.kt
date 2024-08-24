package com.example.koratime.stadiumManager.manageStadium

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.denzcoskun.imageslider.models.SlideModel
import com.example.koratime.utils.DataUtils
import com.example.koratime.adapters.CalendarAdapter
import com.example.koratime.adapters.TimeSlotsAdapter
import com.example.koratime.adapters.parentAdapters.ManageStadiumAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.BookingModel
import com.example.koratime.utils.addBookingToFirestore
import com.example.koratime.utils.addMultipleImagesToFirestore
import com.example.koratime.utils.deleteStadiumFromFirestore
import com.example.koratime.utils.getBookedTimesFromFirestore
import com.example.koratime.utils.getMultipleImagesFromFirestore
import com.example.koratime.utils.removeBookingFromFirestore
import com.example.koratime.utils.uploadMultipleImagesToStorage
import com.example.koratime.model.StadiumModel
import com.google.firebase.firestore.DocumentChange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Suppress("SetTextI18n")
class ManageStadiumViewModel : BasicViewModel<ManageStadiumNavigator>() {
    override val TAG: String
        get() = ManageStadiumViewModel::class.java.simpleName

    companion object {
        const val TAG = "ManageStadiumViewModel"
    }

    var stadium: StadiumModel? = null
    private var listOfUrls = MutableLiveData<List<String>>()
    var timeSlotsArray = MutableLiveData<Array<String>>()

    private var timeSlotsList= mutableListOf<String>()
    private lateinit var availableSlots: MutableList<String>
    private var bookedTimesList = mutableListOf<String>()

    private var timeSlotsAdapter = TimeSlotsAdapter(timeSlotsList, bookedTimesList)
    private var calendarAdapter = CalendarAdapter(emptyList())
    lateinit var manageStadiumAdapter: ManageStadiumAdapter

    private var selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(Date())
    private var selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

    val toastMessage = MutableLiveData<String>()


    fun setUpAdapter() {
        calendarAdapter = CalendarAdapter(generateNextTwoWeeks())

        // create opening and closing list
        timeSlotsList = createListForOpeningTimes(
            stadium!!.opening!!,
            stadium!!.closing!!,
            timeSlotsArray.value!!
        )
        log("TimeSlots List: $timeSlotsList")

        getTimeSlots(selectedDate)

        manageStadiumAdapter = ManageStadiumAdapter(calendarAdapter, timeSlotsAdapter)
        getImagesFromFirestore {
            manageStadiumAdapter.changeImageSlider(it)
        }
    }

    fun adapterCallback() {
        calendarAdapter.onItemClickListener =
            object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(
                date: Date?,
                holder: CalendarAdapter.CalendarViewHolder,
                position: Int
            ) {
                bookedTimesList.clear()
                availableSlots.clear()

                selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(date!!)
                log("Date selected: $selectedDate")
                selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)

                calendarAdapter.changeDate(date)
                manageStadiumAdapter.changeDateTitle(selectedYear)
                getTimeSlots(selectedDate)
            }
        }

        timeSlotsAdapter.onBookClickListener =
            object : TimeSlotsAdapter.OnBookClickListener {
                override fun onclick(
                    slot: String,
                    holder: TimeSlotsAdapter.ViewHolder,
                    position: Int
                ) {
                    addBookingToFirestore(
                        timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                        stadium = stadium!!,
                        date = selectedDate,
                        user = DataUtils.user!!,
                        onSuccessListener = {
                            log("Book Added Successfully to firestore")
                        },
                        onFailureListener = { e ->
                            log("Error Adding Booking to firestore $e")
                        }
                    )
                }

            }

        timeSlotsAdapter.onTimeSlotClickListener =
            object : TimeSlotsAdapter.OnTimeSlotClickListener {
                override fun onclick(
                    slot: String,
                    holder: TimeSlotsAdapter.ViewHolder,
                    position: Int
                ) {
                    removeBookingFromFirestore(
                        timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                        stadiumID = stadium!!.stadiumID!!,
                        date = selectedDate,
                        onSuccessListener = {
                            log("Book Removed Successfully from firestore")
                        },
                        onFailureListener = {
                            log("Error Removing Book from firestore $it")
                        }

                    )
                }
            }

        manageStadiumAdapter.onImagePickerClickListener =
            object : ManageStadiumAdapter.OnImagePickerClickListener {
                override fun onImagePickerClick() {
                    navigator?.openImagePicker()
                }
            }
    }


    private fun getTimeSlots(date: String) {
        getBookedTimesFromFirestore(stadiumID = stadium!!.stadiumID!!, date = date)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    log("Error getting booked times from firestore $error")
                    toastMessage.value = "Error getting booked times from firestore $error"
                } else{
                    for (dc in value!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val booking = dc.document.toObject(BookingModel::class.java)
                                bookedTimesList.add(booking.timeSlot!!)
                                log("Book added ${booking.timeSlot}")
                            }
                            DocumentChange.Type.REMOVED -> {
                                val booking = dc.document.toObject(BookingModel::class.java)
                                bookedTimesList.remove(booking.timeSlot!!)
                                log("Book removed ${booking.timeSlot}")
                            }
                            DocumentChange.Type.MODIFIED -> { log("Modified") }
                        }
                    }
                    log("BookedTimesList: $bookedTimesList")

                    availableSlots = removeBookedListFromOpeningTimes(timeSlotsList, bookedTimesList)
                    log("AvailableSlots List: $availableSlots")
                    timeSlotsAdapter.updateTimeSlots(timeSlotsList, bookedTimesList)
                }

            }

    }

    private fun createListForOpeningTimes(
        openingIndex: Int, closingIndex: Int, timeSlotsArray: Array<String>
    ): MutableList<String> {
        val availableTimeSlots = mutableListOf<String>()

        // Ensure closing index is greater than opening index and within bounds
        if ((openingIndex >= 0) && (closingIndex >= openingIndex) && (closingIndex < timeSlotsArray.size)) {
            for (i in openingIndex..closingIndex) {
                availableTimeSlots.add(timeSlotsArray[i])
            }
        }

        return availableTimeSlots
    }

    private fun removeBookedListFromOpeningTimes(
        allTimeSlots: MutableList<String>, bookedTimeSlots: MutableList<String>
    ): MutableList<String> {
        return allTimeSlots.filterNot { it in bookedTimeSlots }.toMutableList()
    }

    private fun generateNextTwoWeeks(): List<Date> {
        // Initialize calendar to current date
        val calendar = Calendar.getInstance()
        val daysList = mutableListOf<Date>()

        // Loop to generate next 14 days including today
        for (i in 0..13) {
            // Add current date to the list
            daysList.add(calendar.time)
            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return daysList
    }

    private fun uploadImagesToFirestore() {
        addMultipleImagesToFirestore(
            listOfUrls.value!!.toMutableList(),
            stadium!!.stadiumID!!,
            onSuccessListener = {
                Log.e("Firebase", "Images uploaded successfully to firestore")
                showLoading.value = false
            },
            onFailureListener = {
                showLoading.value = false
                Log.e("Firebase", "Error uploading images to firestore")

            }
        )
    }

    fun uploadImagesToStorage(uris: List<Uri>) {
        uploadMultipleImagesToStorage(
            uris = uris,
            stadiumID = stadium!!.stadiumID!!,
            onSuccessListener = { imagesList ->
                log("Images uploaded successfully to storage")
                listOfUrls.value = imagesList
                uploadImagesToFirestore()
//                imagePickerTextView.set("Images Selected")

            },
            onFailureListener = {
                log("Error uploading images to storage $it")
            }
        )
    }

    fun getImagesFromFirestore(callback: (ArrayList<SlideModel>) -> Unit) {
        getMultipleImagesFromFirestore(
            stadiumID = stadium!!.stadiumID!!,
            onSuccessListener = { urls ->
                val imageList = ArrayList<SlideModel>()
                log("List of image urls: $urls")
                urls.forEach { imageList.add(SlideModel(it, "")) }
                callback(imageList) // Invoke the callback with the result
            },
            onFailureListener = {
                log("Failed To get ImagesFrom firestore $it")
                callback(arrayListOf()) // Return an empty list or handle the error
            }
        )
    }

    fun deleteStadium() {
        deleteStadiumFromFirestore(
            stadiumID = stadium!!.stadiumID!!,
            onSuccessListener = {
                Log.e(TAG, "Stadium Removed Successfully from firestore")
                navigator?.closeActivity()
            },
            onFailureListener = {
                Log.e(TAG, "Error Removing Stadium from firestore")
            }
        )
    }

}