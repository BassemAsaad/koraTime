package com.example.koratime.stadiums_user.bookStadium

import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.StadiumModel

class BookingStadiumViewModel : BasicViewModel<BookingStadiumNavigator>() {
    var stadium : StadiumModel?=null
    fun createListForOpeningTimes(openingIndex: Int, closingIndex: Int, timeSlotsArray: Array<String>): List<String> {
        val availableTimeSlots = mutableListOf<String>()

        // Ensure closing index is greater than opening index and within bounds
        if ((openingIndex >= 0) && (closingIndex >= openingIndex) && (closingIndex < timeSlotsArray.size)) {
            for (i in openingIndex..closingIndex) {
                availableTimeSlots.add(timeSlotsArray[i])
            }
        }

        return availableTimeSlots
    }
    fun removeBookedListFromOpeningTimes(allTimeSlots: List<String>, bookedTimeSlots: List<String>): List<String> {
        return allTimeSlots.filterNot { it in bookedTimeSlots }
    }

}