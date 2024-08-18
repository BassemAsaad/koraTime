package com.example.koratime.stadiums.bookingRequests

import android.util.Log
import com.example.koratime.adapters.BookingRequestsAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.getBookingRequestsFromFirestore
import com.example.koratime.model.StadiumModel

class BookingRequestsViewModel : BasicViewModel<BookingRequestsNavigator>() {
    override val TAG: String
        get() = BookingRequestsViewModel::class.java.simpleName

    var stadium = StadiumModel()
    var adapter = BookingRequestsAdapter(emptyList())

    fun adapterSetup() {
        getDates()
    }

    private fun getDates() {
        getBookingRequestsFromFirestore(
            stadiumID = stadium.stadiumID!!,
            onSuccessListener = { querySnapshot ->
                Log.e("Firebase", "datesSlots List: $querySnapshot")
                adapter.changeData(querySnapshot)
            },
            onFailureListener = {
                Log.e("Firebase", "Error fetching booked times", it)
            }
        )
    }
}