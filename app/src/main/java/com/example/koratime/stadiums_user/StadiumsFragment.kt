package com.example.koratime.stadiums_user

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.koratime.R

class StadiumsFragment : Fragment() {

    companion object {
        fun newInstance() = StadiumsFragment()
    }

    private lateinit var viewModel: StadiumsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stadiums, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StadiumsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}