package com.example.koratime.stadiums.stadiumManager

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.basic.BasicFragment
import com.example.koratime.databinding.FragmentStadiumsManagerBinding
import com.example.koratime.home.HomeActivity
import com.example.koratime.model.StadiumModel
import com.example.koratime.registration.login.LoginActivity
import com.example.koratime.stadiums.createStadium.AddStadiumActivity
import com.example.koratime.stadiums.manageStadium.ManageStadiumActivity


class StadiumsManagerFragment :
    BasicFragment<FragmentStadiumsManagerBinding, StadiumsManagerViewModel>(),
    StadiumsManagerNavigator {


    override fun initViewModel(): StadiumsManagerViewModel {
        return ViewModelProvider(this)[StadiumsManagerViewModel::class.java]
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_stadiums_manager
    }


    override fun initView() {
        callback()
    }

    override fun callback() {
        viewModel.apply {
            navigator = this@StadiumsManagerFragment
            toastMessage.observe(this@StadiumsManagerFragment) {
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

        }


    }

    override fun manageStadiumActivity(stadium: StadiumModel?) {
        val intent = Intent(requireContext(), ManageStadiumActivity::class.java)
        intent.putExtra(Constants.STADIUM_MANAGER, stadium)
        startActivity(intent)
    }
    override fun createStadiumActivity() {
        val intent = Intent(requireContext(), AddStadiumActivity::class.java)
        startActivity(intent)
    }

    override fun logout() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        (activity as HomeActivity).finish()
    }

}