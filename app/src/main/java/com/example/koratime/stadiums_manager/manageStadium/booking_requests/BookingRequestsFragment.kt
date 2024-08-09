package com.example.koratime.stadiums_manager.manageStadium.booking_requests

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.adapters.BookingRequestsAdapter
import com.example.koratime.basic.BasicFragment
import com.example.koratime.databinding.FragmentBookingRequestsBinding
import com.example.koratime.model.StadiumModel


@Suppress("DEPRECATION")
class BookingRequestsFragment : BasicFragment<FragmentBookingRequestsBinding, BookingRequestsViewModel>(), BookingRequestsNavigator {
    private lateinit var stadiumModel: StadiumModel
    var adapter = BookingRequestsAdapter(null)

    override fun initViewModel(): BookingRequestsViewModel {
        return ViewModelProvider(this)[BookingRequestsViewModel::class.java]
    }
    override fun getLayoutID(): Int {
        return R.layout.fragment_booking_requests
    }
    override fun initView() {
        (activity as AppCompatActivity).setSupportActionBar(dataBinding.toolbar)
        callback()
    }

    override fun callback() {

        viewModel.apply {
            navigator = this@BookingRequestsFragment
            stadiumModel = arguments?.getParcelable(Constants.STADIUM)!!
            viewModel.stadium =stadiumModel
        }
        viewModel.getDates()
        dataBinding.recyclerView.adapter = viewModel.adapter
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            dataBinding.toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }

        }

    }

}