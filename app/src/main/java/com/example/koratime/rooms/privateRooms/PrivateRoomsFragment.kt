package com.example.koratime.rooms.privateRooms

import android.app.AlertDialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicFragment
import com.example.koratime.databinding.FragmentPrivateRoomsBinding
import com.example.koratime.model.RoomModel
import com.example.koratime.rooms.createRoom.AddRoomActivity
import com.example.koratime.rooms.roomChat.RoomChatActivity
import com.example.koratime.utils.Constants

class PrivateRoomsFragment : BasicFragment<FragmentPrivateRoomsBinding, PrivateRoomsViewModel>(),
    PrivateRoomsNavigator {

    override fun initViewModel(): PrivateRoomsViewModel {
        return ViewModelProvider(this)[PrivateRoomsViewModel::class.java]
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_private_rooms
    }

    override fun initView() {
        callback()
    }//end init

    override fun callback() {
        viewModel.apply {
            navigator = this@PrivateRoomsFragment
            adapterSetup()
            adapterCallback()
        }
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = viewModel.adapter
        }
    }
    override fun openDialog(room: RoomModel?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Remove Friend")
        builder.setMessage("Click ok if u want to remove this friend")
        builder.setPositiveButton("OK") { dialog, _ ->// Handle OK button click
            viewModel.removeRoom(room)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun openRoomChatActivity(room: RoomModel?) {
        val intent = Intent(requireContext(), RoomChatActivity::class.java)
        intent.putExtra(Constants.ROOM, room)
        startActivity(intent)
    }

    override fun openAddRoomActivity() {
        val intent = Intent(requireContext(), AddRoomActivity::class.java)
        startActivity(intent)
    }


}