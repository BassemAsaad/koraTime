package com.example.koratime.stadiums_user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.adapters.StadiumsAdapter
import com.example.koratime.database.getAllStadiumsFromFirestore
import com.example.koratime.database.getUserStadiumFromFirestore
import com.example.koratime.databinding.FragmentStadiumsBinding
import com.example.koratime.model.StadiumModel
import com.example.koratime.stadiums_manager.StadiumsManagerViewModel
import com.example.koratime.stadiums_manager.manageStadium.ManagingStadiumActivity
import com.example.koratime.stadiums_user.bookStadium.BookingStadiumActivity

class StadiumsFragment : Fragment(),StadiumNavigator {

    lateinit var dataBinding : FragmentStadiumsBinding
    lateinit var viewModel : StadiumsViewModel
    val adapter = StadiumsAdapter(null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_stadiums,container,false)
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[StadiumsViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator=this
        dataBinding.recyclerView.adapter = adapter

        adapter.onItemClickListener = object :StadiumsAdapter.OnItemClickListener{
            override fun onItemClick(stadium: StadiumModel?, position: Int) {
                val intent = Intent(requireContext(), BookingStadiumActivity::class.java)
                intent.putExtra(Constants.STADIUM_USER,stadium)
                startActivity(intent)
            }
        }

    }


    override fun onStart() {
        super.onStart()

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