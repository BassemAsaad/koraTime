package com.example.koratime.rooms.publicRooms

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.example.koratime.adapters.PublicRoomsAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.getAllRoomsFromFirestore
import com.example.koratime.model.RoomModel

class PublicRoomsViewModel : BasicViewModel<PublicRoomsNavigator>() {
    override val TAG: String
        get() = PublicRoomsViewModel::class.java.simpleName

    private val password = MutableLiveData<String>()
    private val passwordError = MutableLiveData<String>()
    private val roomPassword = MutableLiveData<String?>()

    val adapter = PublicRoomsAdapter(null)

    fun adapterSetup() {
        getAllRooms()
    }

    fun adapterCallback() {
        adapter.onItemClickListener = object : PublicRoomsAdapter.OnItemClickListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemClick(
                room: RoomModel?,
                position: Int,
                holder: PublicRoomsAdapter.ViewHolder
            ) {
                roomPassword.value = room?.password
                password.value =
                    holder.dataBinding.roomPasswordLayout.editText?.text.toString()

                if (room!!.password != null) {
                    if (checkRoomPassword()) {
                        navigator?.openRoomChatActivity(room)
                    } else {
                        holder.dataBinding.roomPasswordLayout.error = passwordError.value
                    }
                } else {
                    navigator?.openRoomChatActivity(room)
                }

            }
        }
    }

    private fun getAllRooms() {
        getAllRoomsFromFirestore(
            onSuccessListener = { querySnapShot ->
                val rooms = querySnapShot.toObjects(RoomModel::class.java)
                adapter.changeData(rooms)
            }, onFailureListener = {
                log("Error Loading Rooms: $it")
            }
        )
    }

    private fun checkRoomPassword(): Boolean {
        var check = true
        if (password.value != roomPassword.value) {
            check = false
            passwordError.value = ("Wrong Password")
        } else {
            passwordError.value = (null)
        }
        return check
    }


}