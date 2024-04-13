package com.example.koratime.rooms

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.createRoom.AddRoomActivity
import com.example.koratime.databinding.FragmentRoomsBinding

class RoomsFragment : Fragment(),RoomsNavigator {

    lateinit var dataBinding : FragmentRoomsBinding
    lateinit var viewModel : RoomsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_rooms,container,false)
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =  ViewModelProvider(this).get(RoomsViewModel::class.java)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }



     fun initView() {
         dataBinding.vm = viewModel
         viewModel.navigator=this
         addRoomActivity()
    }

    override fun addRoomActivity() {
        dataBinding.createRoomButton.setOnClickListener {
            val intent = Intent(requireContext(), AddRoomActivity::class.java)
            startActivity(intent)
        }

    }


}