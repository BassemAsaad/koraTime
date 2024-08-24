package com.example.koratime.rooms.privateRooms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.koratime.adapters.PrivateRoomsAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.RoomModel
import com.example.koratime.utils.DataUtils
import com.example.koratime.utils.getUserRoomsFromFirestore
import com.example.koratime.utils.removeRoomFromFirestore
import com.google.firebase.firestore.DocumentChange

class PrivateRoomsViewModel : BasicViewModel<PrivateRoomsFragment>() {
    override val TAG: String
        get() = PrivateRoomsFragment::class.java.simpleName
    val password = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()
    var toastMessage = MutableLiveData<String>()
    private val newRoomList = mutableListOf<RoomModel?>()
    val adapter = PrivateRoomsAdapter(null)

    fun adapterSetup() {
        getRooms()
    }

    fun adapterCallback() {
        adapter.onItemClickListener = object : PrivateRoomsAdapter.OnItemClickListener {
            override fun onJoinClick(
                room: RoomModel?,
                position: Int,
                holder: PrivateRoomsAdapter.ViewHolder
            ) {
                val password = holder.dataBinding.roomPasswordLayout.editText?.text.toString()
                if (room!!.password != null) {
                    if (checkRoomPassword(room.password, password)) {
                        navigator?.openRoomChatActivity(room)
                    } else {
                        holder.dataBinding.roomPasswordLayout.error = passwordError.value
                    }
                } else {
                    navigator?.openRoomChatActivity(room)

                }

            }

            override fun onRemoveClick(
                room: RoomModel?,
                position: Int,
                holder: PrivateRoomsAdapter.ViewHolder
            ) {
                    navigator?.openDialog(room)
            }
        }


    }


    fun checkRoomPassword(roomPassword: String?, password: String?): Boolean {
        var check = true
        if (roomPassword != password) {
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

    fun removeRoom(room: RoomModel?) {
        removeRoomFromFirestore(
            roomId = room!!.roomID!!,
            onSuccessListener = {
                Log.e("Firebase", " Room Removed Successfully")
                toastMessage.value = "Room Removed Successfully"
            },
            onFailureListener = {
                Log.e("Firebase", "Error Removing Room $it")
                toastMessage.value = "Error Removing Room"

            }
        )
    }
    private fun getRooms() {
        getUserRoomsFromFirestore(userId = DataUtils.user!!.id!!)
            .addSnapshotListener{ snapshots, error ->
                if (error != null) {
                    log(error.localizedMessage!!)
                    toastMessage.value = "Error loading rooms"
                }else{
                    for (dc in snapshots!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val room = dc.document.toObject(RoomModel::class.java)
                                newRoomList.add(room)
                            }
                            DocumentChange.Type.REMOVED -> {
                                val room = dc.document.toObject(RoomModel::class.java)
                                newRoomList.remove(room)
                            }

                            DocumentChange.Type.MODIFIED -> log("Message Modified")
                        }
                    }
                    adapter.changeData(newRoomList)

                    }

            }
    }

}