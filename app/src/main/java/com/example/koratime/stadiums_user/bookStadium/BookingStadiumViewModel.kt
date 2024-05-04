package com.example.koratime.stadiums_user.bookStadium

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.koratime.R
import com.example.koratime.adapters.TimeSlotAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.getBookedTimesFromFirestore
import com.example.koratime.model.StadiumModel

class BookingStadiumViewModel : BasicViewModel<BookingStadiumNavigator>() {
    var stadium : StadiumModel?=null



     fun generateAvailableTimeSlots(openingIndex: Int, closingIndex: Int, timeSlotsArray: Array<String>): List<String> {
        val availableTimeSlots = mutableListOf<String>()

        // Ensure closing index is greater than opening index and within bounds
        if ((openingIndex >= 0) && (closingIndex >= openingIndex) && (closingIndex < timeSlotsArray.size)) {
            for (i in openingIndex..closingIndex) {
                availableTimeSlots.add(timeSlotsArray[i])
            }
        }

        return availableTimeSlots
    }
    fun filterBookedTimes(allTimeSlots: List<String>, bookedTimeSlots: List<String>): List<String> {
        return allTimeSlots.filterNot { it in bookedTimeSlots }
    }

}