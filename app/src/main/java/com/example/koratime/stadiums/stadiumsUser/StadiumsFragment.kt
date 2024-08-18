package com.example.koratime.stadiums.stadiumsUser

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.basic.BasicFragment
import com.example.koratime.databinding.FragmentStadiumsBinding
import com.example.koratime.model.StadiumModel
import com.example.koratime.registration.login.LoginActivity
import com.example.koratime.stadiums.bookStadium.BookingStadiumActivity

class StadiumsFragment : BasicFragment<FragmentStadiumsBinding, StadiumsViewModel>(),
    StadiumsNavigator {


    override fun initViewModel(): StadiumsViewModel {
        return ViewModelProvider(this)[StadiumsViewModel::class.java]
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_stadiums
    }

    override fun initView() {
        callback()
    }

    override fun callback() {
        viewModel.apply {
            navigator = this@StadiumsFragment
            toastMessage.observe(this@StadiumsFragment) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
            adapterSetup()
            adapterCallback()
        }
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = viewModel.adapter
            userName.text = DataUtils.user!!.userName
            Glide.with(requireContext())
                .load(DataUtils.user!!.profilePicture)
                .into(profilePicture)

            // filter stadiums for search
            searchStadiums.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    // Handle query submission if needed
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.adapter.filterUsers(newText)
                    return true
                }
            })
        }


    }

    override fun bookingStadiumActivity(stadium: StadiumModel?) {
        val intent = Intent(requireContext(), BookingStadiumActivity::class.java)
        intent.putExtra(Constants.STADIUM_USER, stadium)
        startActivity(intent)
    }

    override fun logout() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }

}