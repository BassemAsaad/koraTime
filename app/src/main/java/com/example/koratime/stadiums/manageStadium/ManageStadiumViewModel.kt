package com.example.koratime.stadiums.manageStadium

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.denzcoskun.imageslider.models.SlideModel
import com.example.koratime.DataUtils
import com.example.koratime.adapters.CalendarAdapter
import com.example.koratime.adapters.TimeSlotsForManagerAdapter
import com.example.koratime.adapters.parentAdapters.ManageStadiumAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addBookingToFirestore
import com.example.koratime.database.addMultipleImagesToFirestore
import com.example.koratime.database.deleteStadiumFromFirestore
import com.example.koratime.database.getBookedTimesFromFirestore
import com.example.koratime.database.getMultipleImagesFromFirestore
import com.example.koratime.database.removeBookingFromFirestore
import com.example.koratime.database.uploadMultipleImagesToStorage
import com.example.koratime.model.StadiumModel
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

    private var timeSlotsAdapter = TimeSlotsForManagerAdapter(emptyList(), emptyList())
    private var calendarAdapter = CalendarAdapter(emptyList())
    lateinit var manageStadiumAdapter: ManageStadiumAdapter

    private lateinit var timeSlotsList: List<String>
    private lateinit var availableSlots: List<String>
    private lateinit var bookedTimesList: List<String>

    private var selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(Date())
    private var selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

    val toastMessage = MutableLiveData<String>()


    fun setUpAdapter() {
        calendarAdapter = CalendarAdapter(generateNextTwoWeeks())
        getTimeSlots(selectedDate)
        getImagesFromFirestore {
            manageStadiumAdapter.changeImageSlider(it)
        }
        manageStadiumAdapter = ManageStadiumAdapter(calendarAdapter, timeSlotsAdapter)

    }

    fun adapterCallback() {
        calendarAdapter.onItemClickListener = object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(
                date: Date?,
                holder: CalendarAdapter.CalendarViewHolder,
                position: Int
            ) {
                selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(date!!)
                log("Date selected: $selectedDate")
                selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)

                calendarAdapter.changeDate(date)
                manageStadiumAdapter.changeDateTitle(selectedYear)
                getTimeSlots(selectedDate)
            }
        }
        timeSlotsAdapter.onBookClickListener =
            object : TimeSlotsForManagerAdapter.OnBookClickListener {
                override fun onclick(
                    slot: String,
                    holder: TimeSlotsForManagerAdapter.ViewHolder,
                    position: Int
                ) {
                    addBookingToFirestore(
                        timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                        stadiumID = stadium!!.stadiumID!!,
                        date = selectedDate,
                        user = DataUtils.user!!,
                        onSuccessListener = {
                            log(
                                " ${holder.dataBinding.tvTimeSlot.text} booked on  " +
                                        "$selectedDate from userId: ${DataUtils.user!!.id!!} " +
                                        "to the stadiumID: ${stadium!!.stadiumID}"
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

                            toastMessage.value =
                                "${holder.dataBinding.tvTimeSlot.text} Booked Successfully"
                            getTimeSlots(selectedDate)

                        },
                        onFailureListener = { e ->
                            log("Error Removing Book from firestore $e")
                        }
                    )
                }

            }
        timeSlotsAdapter.onTimeSlotClickListener =
            object : TimeSlotsForManagerAdapter.OnTimeSlotClickListener {
                override fun onclick(
                    slot: String,
                    holder: TimeSlotsForManagerAdapter.ViewHolder,
                    position: Int
                ) {
                    removeBookingFromFirestore(
                        timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                        stadiumID = stadium!!.stadiumID!!,
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
                            toastMessage.value =
                                "${holder.dataBinding.tvTimeSlot.text} Book Removed Successfully"
                            getTimeSlots(selectedDate)
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

    private fun getTimeSlots(date: String) {
        getBookedTimesFromFirestore(
            stadiumID = stadium!!.stadiumID!!,
            date = date,
            onSuccessListener = { bookedList ->

                bookedTimesList = bookedList
                log("Booked times $bookedTimesList")

                // create opening and closing list
                timeSlotsList = createListForOpeningTimes(
                    stadium!!.opening!!,
                    stadium!!.closing!!,
                    timeSlotsArray.value!!
                )
                log("TimeSlots List: $timeSlotsList")

                availableSlots = removeBookedListFromOpeningTimes(timeSlotsList, bookedTimesList)
                log("AvailableSlots List: $availableSlots")

                timeSlotsAdapter.updateTimeSlots(timeSlotsList, bookedTimesList)

            },
            onFailureListener = { e ->
                log("Error fetching booked times $e")
            }
        )
    }

    private fun createListForOpeningTimes(
        openingIndex: Int, closingIndex: Int, timeSlotsArray: Array<String>
    ): List<String> {
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
        allTimeSlots: List<String>, bookedTimeSlots: List<String>
    ): List<String> {
        return allTimeSlots.filterNot { it in bookedTimeSlots }
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
    /*

            dataBinding.imagePickerTextView.text = " No Image Selected"
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            log("PhotoPicker: No media selected")
     */

}