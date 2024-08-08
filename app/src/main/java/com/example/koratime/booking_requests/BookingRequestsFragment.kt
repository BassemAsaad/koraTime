package com.example.koratime.booking_requests

import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicFragment
import com.example.koratime.databinding.FragmentBookingRequestsBinding


class BookingRequestsFragment : BasicFragment<FragmentBookingRequestsBinding, BookingRequestsViewModel>() {
    override fun initViewModel(): BookingRequestsViewModel {
        return ViewModelProvider(this)[BookingRequestsViewModel::class.java]
    }
    override fun getLayoutID(): Int {
        return R.layout.fragment_booking_requests
    }
    override fun initView() {
        callback()
    }

    override fun callback() {

    }



}