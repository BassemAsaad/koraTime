package com.example.koratime.stadiums_manager.manageStadium

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.adapters.TimeSlotAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityManagingStadiumBinding
import com.example.koratime.model.StadiumModel

@Suppress("DEPRECATION")
class ManagingStadiumActivity : BasicActivity<ActivityManagingStadiumBinding,ManagingStadiumViewModel>(),ManagingStadiumNavigator{
    private lateinit var stadiumModel : StadiumModel
    private lateinit var adapter : TimeSlotAdapter

    override fun getLayoutID(): Int {
        return R.layout.activity_managing_stadium
    }

    override fun initViewModel(): ManagingStadiumViewModel {
        return ViewModelProvider(this)[ManagingStadiumViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }
    override fun initView() {
        viewModel.navigator=this
        dataBinding.vm = viewModel
        stadiumModel = intent.getParcelableExtra(Constants.STADIUM_MANAGER)!!
        viewModel.stadium = stadiumModel

        setSupportActionBar(dataBinding.toolbar)
        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Enable title on Toolbar
        supportActionBar?.title = stadiumModel.stadiumName + " Stadium"
        supportActionBar?.setDisplayShowTitleEnabled(true)

        val timeList = generateAvailableTimeSlots(stadiumModel.opening!!,stadiumModel.closing!!
            ,resources.getStringArray(R.array.time_slots))

//        adapter = TimeSlotAdapter(timeList,b)

        dataBinding.recyclerView.adapter =adapter

    }
    fun generateAvailableTimeSlots(openingIndex: Int, closingIndex: Int, timeSlotsArray: Array<String>): List<String> {
        val availableTimeSlots = mutableListOf<String>()

        // Ensure closing index is greater than opening index and within bounds
        if (openingIndex >= 0 && closingIndex >= openingIndex && closingIndex < timeSlotsArray.size) {
            for (i in openingIndex..closingIndex) {
                availableTimeSlots.add(timeSlotsArray[i])
            }
        }

        return availableTimeSlots
    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }
}