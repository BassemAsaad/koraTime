package com.example.koratime.bookings

import com.example.koratime.adapters.BookingRequestsUserAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.BookingModel
import com.example.koratime.utils.DataUtils
import com.example.koratime.utils.getUserBookingRequestsFromFirestore

class BookingsViewModel : BasicViewModel<BookingsNavigator>() {
    override val TAG: String
        get() = "BookingsViewModel"
    private var bookingsList = mutableListOf<BookingModel>()
    var bookingsAdapter = BookingRequestsUserAdapter(bookingsList)

    fun adapterSetup(){
        getBookings()
    }
    fun adapterCallback(){

    }
    private fun getBookings(){
        getUserBookingRequestsFromFirestore(
            DataUtils.user!!,
            onSuccessListener = {
                log("Bookings returned successfully")
                for (document in it) {
                    val booking = document.toObject(BookingModel::class.java)
                    bookingsList.add(booking)
                }
                bookingsAdapter.changeData(bookingsList)
            },
            onFailureListener = {
                log("Error getting Bookings: $it")
            }
        )
    }
}
