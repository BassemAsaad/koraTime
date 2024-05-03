package com.example.koratime.aa.test2

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityManagingStadiumTimesBinding

class ManagingStadiumTimesActivity : BasicActivity<ActivityManagingStadiumTimesBinding,ManagingStadiumTimesViewModel>(),ManagingStadiumTimesNavigator {

    private lateinit var adapter:TimeSlotAdapter


    override fun getLayoutID(): Int {
        return R.layout.activity_managing_stadium_times
    }

    override fun initViewModel(): ManagingStadiumTimesViewModel {
        return ViewModelProvider(this)[ManagingStadiumTimesViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }


    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel

        setupSpinners()
        dataBinding.btnGenerateSlots.setOnClickListener {
            val timeSlots = generateTimeSlots(
                dataBinding.opening.selectedItem.toString(),
                dataBinding.closing.selectedItem.toString()
            )

            val allSlots = timeSlots
            Log.e("firebase","$allSlots")
            adapter = TimeSlotAdapter(allSlots)

            dataBinding.recyclerView.adapter =adapter
            adapter.onBookClickListener = object :TimeSlotAdapter.OnBookClickListener{
                override fun onclick(slot: String,holder: TimeSlotAdapter.ViewHolder, position: Int) {
                    Log.e("Firebase","Booked From $slot + $position")
                    holder.btnBook.setTextColor(Color.RED)
                    holder.btnBook.text = "Booked"
                    holder.btnBook.isEnabled=false
                }
            }

        }

        //i want to send the opening and closing indexes to firestore
        //example i choose the opening at
        //12pm [index0] and closing at 11pm[index11]
        //i want to send those indexes to firestore
        //i have in my data class
        //opening of type int
        //closing of type int
    }

    private fun setupSpinners() {
        val timeSlots = resources.getStringArray(R.array.time_slots)

        val adapterSlots = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSlots)
        adapterSlots.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        dataBinding.opening.adapter = adapterSlots
        dataBinding.closing.adapter = adapterSlots


        // Add listeners to update closing time spinner based on opening time spinner selection
        dataBinding.opening.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateClosingTimeSpinner(dataBinding.opening, dataBinding.closing)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }


    }



    // Update closing time spinner based on opening time spinner
    private fun updateClosingTimeSpinner(openingSpinner: Spinner, closingSpinner: Spinner) {
        val openingTime = openingSpinner.selectedItem.toString()
        val openingIndex = openingSpinner.selectedItemPosition
        val timeSlots= resources.getStringArray(R.array.time_slots)

        // Generate closing time slots starting from one hour after the opening time
        val closingTimeSlots = timeSlots.sliceArray((openingIndex + 1) until timeSlots.size).toMutableList()

        val closingAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, closingTimeSlots)
        closingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        closingSpinner.adapter = closingAdapter

        // Adjust selection if closing time is at the end of time slots array
        val selectedClosingTimeIndex = if (closingTimeSlots.contains(openingTime)) {
            closingTimeSlots.indexOf(openingTime) + 1
        } else {
            0
        }
        closingSpinner.setSelection(selectedClosingTimeIndex)
    }

    //generate time slots for morning and afternoon intervals
    private fun generateTimeSlots(open: String, close: String): List<String> {
        val timeSlots = resources.getStringArray(R.array.time_slots)

        val openIndexMorning = timeSlots.indexOf(open)
        val closeIndexMorning = timeSlots.indexOf(close)


        val slots = mutableListOf<String>()

        // For morning session
        if (openIndexMorning >= 0) {
            val adjustedCloseIndexMorning = closeIndexMorning + 1
            for (i in openIndexMorning until adjustedCloseIndexMorning) {
                slots.add(timeSlots[i])
            }
        }

        return slots
    }
}