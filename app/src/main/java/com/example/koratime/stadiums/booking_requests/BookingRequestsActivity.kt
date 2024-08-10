package com.example.koratime.stadiums.booking_requests

import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityBookingRequestsBinding
import com.example.koratime.model.StadiumModel


@Suppress("DEPRECATION")
class BookingRequestsActivity : BasicActivity<ActivityBookingRequestsBinding, BookingRequestsViewModel>(),
    BookingRequestsNavigator {
    override val TAG: String
        get() = "BookingRequestsActivity"
    private lateinit var stadiumModel: StadiumModel

    override fun initViewModel(): BookingRequestsViewModel {
        return ViewModelProvider(this)[BookingRequestsViewModel::class.java]
    }
    override fun getLayoutID(): Int {
        return R.layout.activity_booking_requests
    }
    override fun initView() {
        setSupportActionBar(dataBinding.toolbar)
        callback()
    }

    override fun callback() {
        viewModel.apply {
            navigator = this@BookingRequestsActivity
            stadiumModel = intent.getParcelableExtra(Constants.STADIUM)!!
            viewModel.stadium =stadiumModel
            viewModel.getDates()
            dataBinding.recyclerView.adapter = viewModel.adapter
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "Requests"
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked
        onBackPressed()
        return true
    }
}