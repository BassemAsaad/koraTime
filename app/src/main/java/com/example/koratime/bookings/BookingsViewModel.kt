package com.example.koratime.bookings

import com.example.koratime.adapters.BookingRequestsUserAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.BookingModel
import com.example.koratime.utils.DataUtils
import com.example.koratime.utils.getUserBookingRequestsFromFirestore
import com.google.firebase.firestore.QuerySnapshot

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
            onSuccessListener = { taskResults->
                log("Bookings returned successfully")
                log(taskResults.toString())
                for (task in taskResults) {
                    if (task.isSuccessful) {
                        val query = task.result as QuerySnapshot
                        bookingsList.addAll(query.toObjects(BookingModel::class.java))
                    }
                }
                bookingsAdapter.changeData(bookingsList)
            },
            onFailureListener = {
                log("Error getting Bookings: $it")
            }
        )
    }
}
