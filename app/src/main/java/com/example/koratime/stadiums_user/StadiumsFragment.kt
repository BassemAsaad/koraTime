package com.example.koratime.stadiums_user

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.StadiumsAdapter
import com.example.koratime.basic.BasicFragment
import com.example.koratime.database.getAllStadiumsFromFirestore
import com.example.koratime.databinding.FragmentStadiumsBinding
import com.example.koratime.model.StadiumModel
import com.example.koratime.registration.log_in.LoginActivity
import com.example.koratime.stadiums_user.bookStadium.BookingStadiumActivity

class StadiumsFragment : BasicFragment<FragmentStadiumsBinding,StadiumsViewModel>(),StadiumsNavigator {

    val adapter = StadiumsAdapter(null)

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
        viewModel.navigator = this
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = adapter
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
                    adapter.filterUsers(newText)
                    return true
                }
            })
        }
        adapter.onItemClickListener = object :StadiumsAdapter.OnItemClickListener{
            override fun onItemClick(stadium: StadiumModel?, position: Int) {
                val intent = Intent(requireContext(), BookingStadiumActivity::class.java)
                intent.putExtra(Constants.STADIUM_USER,stadium)
                startActivity(intent)
            }
        }

    }

    override fun Logout() {
        val intent  = Intent(requireContext(),LoginActivity::class.java)
        startActivity(intent)
    }
    override fun onStart() {
        super.onStart()
        getAllStadiums()
    }

    private fun getAllStadiums() {
        getAllStadiumsFromFirestore(
            onSuccessListener = {querySnapShot->
                val stadiums = querySnapShot.toObjects(StadiumModel::class.java)
                adapter.changeData(stadiums)
            },
            onFailureListener = {
                Log.e("Stadiums Adapter: ", it.localizedMessage!!.toString())
                Toast.makeText(requireContext(), "Error Loading Stadiums", Toast.LENGTH_SHORT).show()
            }
        )
    }

}