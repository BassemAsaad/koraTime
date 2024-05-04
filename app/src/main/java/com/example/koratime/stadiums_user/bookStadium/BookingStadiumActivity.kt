package com.example.koratime.stadiums_user.bookStadium

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.TimeSlotAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.addBookingToFirestore
import com.example.koratime.database.getBookedTimesFromFirestore
import com.example.koratime.databinding.ActivityBookingStadiumBinding
import com.example.koratime.model.StadiumModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
class BookingStadiumActivity : BasicActivity<ActivityBookingStadiumBinding,BookingStadiumViewModel>(),BookingStadiumNavigator {

    private lateinit var stadiumModel : StadiumModel
    private lateinit var adapter : TimeSlotAdapter
    private lateinit var timeList :List<String>
    private var selectedDate: String? = null
    override fun getLayoutID(): Int {
        return R.layout.activity_booking_stadium
    }

    override fun initViewModel(): BookingStadiumViewModel {
        return ViewModelProvider(this)[BookingStadiumViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }
    override fun initView() {
        viewModel.navigator=this
        dataBinding.vm = viewModel

        stadiumModel = intent.getParcelableExtra(Constants.STADIUM_USER)!!
        viewModel.stadium = stadiumModel

        setSupportActionBar(dataBinding.toolbar)
        // Enable back button on Toolbar and title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Enable title on Toolbar
        supportActionBar?.title = stadiumModel.stadiumName + " Stadium"
        supportActionBar?.setDisplayShowTitleEnabled(true)


        // get booked times i didn't use it yet
        getBookedTimesFromFirestore(stadiumID = stadiumModel.stadiumID!!, date = selectedDate!!,
            onSuccessListener = { slotNames ->
                // Handle the successful retrieval of slot names
            },
            onFailureListener = { exception ->
                // Handle the failure
            }
        )

         timeList = generateAvailableTimeSlots(stadiumModel.opening!!,stadiumModel.closing!!,resources.getStringArray(R.array.time_slots))

        adapter = TimeSlotAdapter(timeList)

        dataBinding.recyclerView.adapter =adapter

        selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(Date())
        // Add an OnDateChangeListener to the CalendarView
        dataBinding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate = String.format("%02d_%02d_%04d", month + 1, dayOfMonth, year)
            Log.e("Firebase"," Date selected: $selectedDate  ")

            // Update the RecyclerView when day changes
            adapter.updateTimeSlots(timeList)
            dataBinding.recyclerView.adapter =adapter
        }

        // Set up click listener for booking button in the adapter
        adapter.onBookClickListener = object : TimeSlotAdapter.OnBookClickListener {
            override fun onclick(slot: String, holder: TimeSlotAdapter.ViewHolder, position: Int) {

                addBookingToFirestore(timeSlot = holder.tvTimeSlot.text.toString(),
                    stadiumID = stadiumModel.stadiumID!!,
                    date = selectedDate!!, userId = DataUtils.user!!.id!!,
                    onSuccessListener = {
                        holder.tvTimeSlot.isEnabled=false
                        holder.tvTimeSlot.setTextColor((Color.GRAY))
                        holder.btnBook.isEnabled=false
                        holder.btnBook.text= "Booked"
                        holder.btnBook.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                        Log.e("Firebase"," ${holder.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} to the stadiumID: ${stadiumModel.stadiumID}") },
                    onFailureListener = {e->
                        Log.e("Firebase"," Error:  ${holder.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} ",e) }
                )

            }
        }


    }


    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

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
}