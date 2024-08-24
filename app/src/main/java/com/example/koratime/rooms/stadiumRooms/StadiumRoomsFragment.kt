package com.example.koratime.rooms.stadiumRooms

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.utils.Constants
import com.example.koratime.R
import com.example.koratime.basic.BasicFragment
import com.example.koratime.databinding.FragmentStadiumRoomsBinding
import com.example.koratime.model.RoomModel
import com.example.koratime.rooms.roomChat.RoomChatActivity

class StadiumRoomsFragment : BasicFragment<FragmentStadiumRoomsBinding, StadiumRoomsViewModel>(),
    StadiumRoomsNavigator {

    override fun initViewModel(): StadiumRoomsViewModel {
        return ViewModelProvider(this)[StadiumRoomsViewModel::class.java]
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_stadium_rooms
    }


    override fun initView() {
        callback()
    }


    override fun callback() {
        viewModel.apply {
            navigator = this@StadiumRoomsFragment
            adapterCallback()
        }
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = viewModel.adapter
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.adapterSetup()
    }

    override fun openRoomChatActivity(room: RoomModel?) {
        val intent = Intent(requireContext(), RoomChatActivity::class.java)
        intent.putExtra(Constants.ROOM, room)
        startActivity(intent)
    }
}