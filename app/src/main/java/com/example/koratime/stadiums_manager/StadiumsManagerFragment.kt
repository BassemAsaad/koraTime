package com.example.koratime.stadiums_manager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.StadiumsAdapter
import com.example.koratime.basic.BasicFragment
import com.example.koratime.database.getUserStadiumFromFirestore
import com.example.koratime.databinding.FragmentStadiumsManagerBinding
import com.example.koratime.home.HomeActivity
import com.example.koratime.model.StadiumModel
import com.example.koratime.registration.log_in.LoginActivity
import com.example.koratime.stadiums_manager.createStadium.AddStadiumActivity
import com.example.koratime.stadiums_manager.manageStadium.ManageStadiumFragment


class StadiumsManagerFragment :
    BasicFragment<FragmentStadiumsManagerBinding, StadiumsManagerViewModel>(),
    StadiumsManagerNavigator {

    val adapter = StadiumsAdapter(null)

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
        viewModel.navigator = this
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = adapter
            userName.text = DataUtils.user!!.userName
            Glide.with(requireContext())
                .load(DataUtils.user!!.profilePicture)
                .into(profilePicture)
        }

        adapter.onItemClickListener = object : StadiumsAdapter.OnItemClickListener {
            override fun onItemClick(stadium: StadiumModel?, position: Int) {
                val manageStadiumFragment = ManageStadiumFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constants.STADIUM_MANAGER, stadium)
                manageStadiumFragment.arguments = bundle
                (activity as HomeActivity).addFragment(manageStadiumFragment, true)
            }
        }
    }


    override fun createStadiumActivity() {
        val intent = Intent(requireContext(), AddStadiumActivity::class.java)
        startActivity(intent)
    }

    override fun Logout() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        getUserStadiums()

    }

    private fun getUserStadiums() {
        getUserStadiumFromFirestore(
            DataUtils.user!!.id,
            onSuccessListener = { querySnapShot ->
                val stadiums = querySnapShot.toObjects(StadiumModel::class.java)
                adapter.changeData(stadiums)
            },
            onFailureListener = {
                Log.e("Stadiums Adapter: ", it.localizedMessage!!.toString())
                Toast.makeText(requireContext(), "Error Loading Stadiums", Toast.LENGTH_SHORT)
                    .show()
            }
        )
    }

}