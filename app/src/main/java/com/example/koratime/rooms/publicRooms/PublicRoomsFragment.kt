package com.example.koratime.rooms.publicRooms

import android.content.Intent
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.basic.BasicFragment
import com.example.koratime.databinding.FragmentPublicRoomsBinding
import com.example.koratime.model.RoomModel
import com.example.koratime.rooms.roomChat.RoomChatActivity

class PublicRoomsFragment : BasicFragment<FragmentPublicRoomsBinding, PublicRoomsViewModel>(),
    PublicRoomsNavigator {

    override fun initViewModel(): PublicRoomsViewModel {
        return ViewModelProvider(this)[PublicRoomsViewModel::class.java]
    }


    override fun getLayoutID(): Int {
        return R.layout.fragment_public_rooms
    }

    override fun initView() {
        callback()

    }

    override fun callback() {
        viewModel.apply {
            navigator = this@PublicRoomsFragment
            adapterSetup()
            adapterCallback()
        }
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = viewModel.adapter
            searchRooms.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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


    override fun openRoomChatActivity(room: RoomModel?) {
        val intent = Intent(requireContext(), RoomChatActivity::class.java)
        intent.putExtra(Constants.ROOM, room)
        startActivity(intent)
    }


}