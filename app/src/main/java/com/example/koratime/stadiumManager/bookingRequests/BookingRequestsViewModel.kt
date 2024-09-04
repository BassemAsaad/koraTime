package com.example.koratime.stadiumManager.bookingRequests

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.koratime.R
import com.example.koratime.adapters.BookingRequestsManagerAdapter
import com.example.koratime.adapters.BookingRequestsManagerAdapter.ViewHolder
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.BookingModel
import com.example.koratime.model.StadiumModel
import com.example.koratime.utils.acceptBookingRequest
import com.example.koratime.utils.getBookingRequestsFromFirestore
import com.example.koratime.utils.rejectBookingRequest
import com.google.firebase.firestore.QuerySnapshot

@SuppressLint("SetTextI18n")
class BookingRequestsViewModel : BasicViewModel<BookingRequestsNavigator>() {
    override val TAG: String
        get() = BookingRequestsViewModel::class.java.simpleName

    var stadium = StadiumModel()
    var adapter = BookingRequestsManagerAdapter(emptyList())
    private var bookingsList = mutableListOf<BookingModel>()

    fun adapterSetup() {
        getDates()
    }
    fun adapterCallback(){
        adapter.onItemClickListener = object : BookingRequestsManagerAdapter.OnItemClickListener {
            override fun onAcceptClick(
                holder: ViewHolder,
                position: Int,
                stadiumId : String,
                userId: String,
                date: String,
                timeSlot: String
            ) {
                acceptBookingRequest(
                    stadiumID = stadiumId,
                    userID = userId,
                    date = date,
                    timeSlot = timeSlot,
                    onSuccessListener = {
                        log("Booking request accepted")
                        holder.dataBinding.apply {
                            buttonOne.isEnabled = false
                            buttonOne.text = "Accepted"
                            buttonOne.setTextColor(ContextCompat.getColor(root.context, R.color.green))
                            buttonTwo.visibility = View.GONE

                        }
                    },
                    onFailureListener = {
                        log("Error accepting booking request $it")
                    }
                )
            }

            override fun onRejectClick(
                holder: ViewHolder,
                position: Int,
                stadiumId : String,
                userId: String,
                date: String,
                timeSlot: String
            ) {
                rejectBookingRequest(
                    stadiumID = stadiumId,
                    userID = userId,
                    date = date,
                    timeSlot = timeSlot,
                    onSuccessListener = {
                        log("Booking request rejected")
                        holder.dataBinding.apply {
                            buttonOne.isEnabled = false
                            buttonOne.text = "Rejected"
                            buttonOne.setTextColor(ContextCompat.getColor(root.context, R.color.red))
                            buttonTwo.visibility = View.GONE

                        }
                    },
                    onFailureListener = {
                        log("Error rejecting booking request $it")
                    }
                )
            }
        }
    }

    private fun getDates() {
        getBookingRequestsFromFirestore(
            stadiumID = stadium.stadiumID!!,
            onSuccessListener = { taskResults->
                log("Bookings returned successfully")
                for (task in taskResults) {
                    if (task.isSuccessful) {
                        val query = task.result as QuerySnapshot
                        bookingsList.addAll(query.toObjects(BookingModel::class.java))
                    }
                }
                adapter.changeData(bookingsList)
            },
            onFailureListener = {
                Log.e("Firebase", "Error fetching booked times", it)
            }
        )
    }
}