package com.example.koratime.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@Parcelize
data class BookingModel(
    val userId: String? = null,
    val userName: String? = null,
    val profilePicture: String? = null,
    var date: String? = null,
    val timeSlot: String? = null,
    val status: String? = null,
    val dateTime: Long? = null,
) :Parcelable{
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