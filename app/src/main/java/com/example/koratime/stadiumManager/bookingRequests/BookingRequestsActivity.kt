package com.example.koratime.stadiumManager.bookingRequests

import androidx.lifecycle.ViewModelProvider
import com.example.koratime.utils.Constants
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityBookingRequestsBinding
import com.example.koratime.model.StadiumModel


@Suppress("DEPRECATION")
class BookingRequestsActivity :
    BasicActivity<ActivityBookingRequestsBinding, BookingRequestsViewModel>(),
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
        callback()
    }

    override fun callback() {
        setSupportActionBar(dataBinding.toolbar)
        viewModel.apply {
            navigator = this@BookingRequestsActivity
            stadiumModel = intent.getParcelableExtra(Constants.STADIUM_BOOKING_REQUESTS)!!
            stadium = stadiumModel
            adapterSetup()
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "Requests"
        }
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = viewModel.adapter
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked
        onBackPressed()
        return true
    }
}