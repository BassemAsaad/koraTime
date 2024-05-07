package com.example.koratime.rooms.stadiumRooms

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.PublicRoomsAdapter
import com.example.koratime.database.getStadiumRoomFromFirestore
import com.example.koratime.databinding.FragmentStadiumRoomsBinding
import com.example.koratime.model.RoomModel
import com.example.koratime.rooms.room_chat.RoomChatActivity

class StadiumRoomsFragment : Fragment(), StadiumRoomsNavigator {

    private lateinit var dataBinding : FragmentStadiumRoomsBinding
    private lateinit var viewModel : StadiumRoomsViewModel
    private val adapter = PublicRoomsAdapter(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_stadium_rooms,container,false)
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[StadiumRoomsViewModel::class.java]


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


     fun initView() {
         dataBinding.vm = viewModel
         viewModel.navigator=this


         dataBinding.recyclerView.adapter = adapter

         adapter.onItemClickListener = object : PublicRoomsAdapter.OnItemClickListener{
             @SuppressLint("SuspiciousIndentation")
             override fun onItemClick(
                 room: RoomModel?,
                 position: Int,
                 holder: PublicRoomsAdapter.ViewHolder
             ) {
                 val intent = Intent(requireContext(),RoomChatActivity::class.java)
                 intent.putExtra(Constants.ROOM,room)
                 startActivity(intent)
             }
         }
    }//end init



    override fun onStart() {
        super.onStart()
        getStadiumRoomFromFirestore(
            playerID = DataUtils.user!!.id!!,
            onSuccessListener = {querySnapShot->
                val rooms = querySnapShot.toObjects(RoomModel::class.java)
                adapter.changeData(rooms)
                Log.e("Firebase "," Adapter Working")

            }
            , onFailureListener = {
                Log.e("Firebase"," Error getting Rooms:" ,it)
            }
        )
    }



}