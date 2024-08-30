package com.example.koratime.bookings

import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicFragment
import com.example.koratime.databinding.FragmentBookingsBinding

class BookingsFragment : BasicFragment<FragmentBookingsBinding,BookingsViewModel>(),BookingsNavigator {
    override fun initViewModel(): BookingsViewModel {
        return ViewModelProvider(this)[BookingsViewModel::class.java]
    }
    override fun getLayoutID(): Int {
        return R.layout.fragment_bookings
    }
    override fun initView() {
        callback()
    }

    override fun callback() {
        viewModel.apply {
            navigator = this@BookingsFragment
        }
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = viewModel.bookingsAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.adapterSetup()

    }



}