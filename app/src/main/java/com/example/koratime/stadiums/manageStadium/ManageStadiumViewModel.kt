package com.example.koratime.stadiums.manageStadium

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.koratime.adapters.CalendarAdapter
import com.example.koratime.adapters.TimeSlotsForManagerAdapter
import com.example.koratime.adapters.parentAdapters.ManageStadiumAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addMultipleImagesToFirestore
import com.example.koratime.database.deleteStadiumFromFirestore
import com.example.koratime.model.StadiumModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ManageStadiumViewModel : BasicViewModel<ManageStadiumNavigator>() {
    companion object{
        const val TAG = "ManageStadiumViewModel"
        const val VISIBLE = 0
        const val INVISIBLE = 4
        const val GONE = 8
    }

    private var timeSlotsAdapter = TimeSlotsForManagerAdapter(emptyList(), emptyList())
    private var calendarAdapter = CalendarAdapter(emptyList())
    private lateinit var manageStadiumAdapter : ManageStadiumAdapter

    private lateinit var timeSlotsList: List<String>
    private lateinit var availableSlots: List<String>
    private lateinit var bookedTimesList: List<String>

    private var selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(Date())
    private var selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

    private val slideImageList = mutableListOf<String>()
    var stadium: StadiumModel? = null
    var listOfUrls = MutableLiveData<List<String>>()




    fun deleteStadium() {
        deleteStadiumFromFirestore(
            stadiumID = stadium!!.stadiumID!!,
            onSuccessListener = {
                Log.e(TAG,"Stadium Removed Successfully from firestore")
                navigator?.closeActivity()
            },
            onFailureListener = {
                Log.e(TAG,"Error Removing Stadium from firestore")
            }
        )
    }
    fun adapterCallBacks(){

    }

    fun createListForOpeningTimes(
        openingIndex: Int,
        closingIndex: Int,
        timeSlotsArray: Array<String>
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
    fun addImageUrlsToFirestore() {
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
    fun removeBookedListFromOpeningTimes(
        allTimeSlots: List<String>,
        bookedTimeSlots: List<String>
    ): List<String> {
        return allTimeSlots.filterNot { it in bookedTimeSlots }
    }
    fun generateNextTwoWeeks(): List<Date> {
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
}