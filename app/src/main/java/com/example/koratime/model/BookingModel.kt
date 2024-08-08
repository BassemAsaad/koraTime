package com.example.koratime.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BookingModel(
    val userId: String? = null,
    val userName: String? = null,
    val profilePicture: String? = null,
    val date: String? = null,
    val timeSlot: String? = null,
    val status: String? = null,
    val dateTime: Long? = null,
){
    companion object{
        const val COLLECTION_NAME= "Bookings"
        const val SUB_COLLECTION_NAME = "TimeSlot"
        const val FIELD_STATUS = "status"
    }
    fun formatTime(): String {
        val date = Date(dateTime!!)
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return simpleDateFormat.format(date)

    }
}
