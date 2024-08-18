package com.example.koratime.rooms.privateRooms

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.adapters.PrivateRoomsAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.chat.ChatViewModel
import com.example.koratime.database.getUserRoomsFromFirestore
import com.example.koratime.database.removeRoomFromFirestore
import com.example.koratime.model.RoomModel
import com.example.koratime.rooms.roomChat.RoomChatActivity

class PrivateRoomsViewModel : BasicViewModel<PrivateRoomsFragment>() {
    override val TAG: String
        get() = PrivateRoomsFragment::class.java.simpleName
    val password = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()
    val roomPassword = MutableLiveData<String?>()
    var toastMessage = MutableLiveData<String>()
    val adapter = PrivateRoomsAdapter(null)

    fun adapterSetup() {
        getRooms()
    }
    fun adapterCallback() {
        adapter.onItemClickListener = object : PrivateRoomsAdapter.OnItemClickListener {
            override fun onItemClick(
                room: RoomModel?,
                position: Int,
                holder: PrivateRoomsAdapter.ViewHolder
            ) {
                holder.dataBinding.removeRoom.setOnClickListener {
                    removeRoomFromFirestore(
                        roomId = room!!.roomID!!,
                        onSuccessListener = {
                            Log.e("Firebase", " Room Removed Successfully")
                        },
                        onFailureListener = {
                            Log.e("Firebase", "Error Removing Room")

                        }
                    )

                }

                roomPassword.value = room!!.password
                password.value =
                    holder.dataBinding.roomPasswordLayout.editText?.text.toString()
                if (room.password != null) {
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


    fun checkRoomPassword(): Boolean {
        var check = true
        if (password.value != roomPassword.value) {
            check = false
            passwordError.value = ("Wrong Password")
        } else {
            passwordError.value = (null)
        }
        return check
    }

    fun createRoom() {
        navigator?.openAddRoomActivity()
    }
    private fun getRooms(){
        getUserRoomsFromFirestore(
            userId = DataUtils.user!!.id!!,
            onSuccessListener = { querySnapShot ->
                val rooms = querySnapShot.toObjects(RoomModel::class.java)
                adapter.changeData(rooms)
            }, onFailureListener = {
                log("Error getting rooms: $it")
            }
        )
    }

}