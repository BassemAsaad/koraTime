package com.example.koratime.stadiums.booking_requests

import android.util.Log
import com.example.koratime.adapters.BookingRequestsAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.getBookingRequestsFromFirestore
import com.example.koratime.model.StadiumModel

class BookingRequestsViewModel : BasicViewModel<BookingRequestsNavigator>() {
    var stadium = StadiumModel()
    var adapter = BookingRequestsAdapter(emptyList())

    fun getDates(){
        getBookingRequestsFromFirestore(
            stadiumID = stadium.stadiumID!!,
            onSuccessListener = {querySnapshot->
                Log.e("Firebase", "datesSlots List: $querySnapshot")
                adapter.changeData(querySnapshot)
            },
            onFailureListener = {
                Log.e("Firebase", "Error fetching booked times", it)
            }
        )
    }
}