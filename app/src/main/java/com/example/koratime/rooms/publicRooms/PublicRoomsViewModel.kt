package com.example.koratime.rooms.publicRooms

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.example.koratime.adapters.PublicRoomsAdapter
import com.example.koratime.adapters.parentAdapters.PublicRoomsParentAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.utils.getAllRoomsFromFirestore
import com.example.koratime.model.RoomModel
import com.google.firebase.firestore.DocumentChange

class PublicRoomsViewModel : BasicViewModel<PublicRoomsNavigator>() {
    override val TAG: String
        get() = PublicRoomsViewModel::class.java.simpleName

    private val password = MutableLiveData<String>()
    private val passwordError = MutableLiveData<String>()
    private val roomPassword = MutableLiveData<String?>()
    private val newRoomList = mutableListOf<RoomModel?>()
    private val adapter = PublicRoomsAdapter(emptyList())
    lateinit var parentAdapter: PublicRoomsParentAdapter

    fun adapterSetup() {
        getAllRooms()
        parentAdapter = PublicRoomsParentAdapter(adapter)
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
        getAllRoomsFromFirestore()
            .addSnapshotListener { value, error ->
                if (error != null) {

                    log(error.localizedMessage!!)
                }else{
                    for (dc in value!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val room = dc.document.toObject(RoomModel::class.java)
                                newRoomList.add(room)
                            }
                            DocumentChange.Type.REMOVED -> {
                                val room = dc.document.toObject(RoomModel::class.java)
                                newRoomList.remove(room)
                            }
                            DocumentChange.Type.MODIFIED -> log("Room Modified")
                        }
                    }
                    adapter.changeData(newRoomList)
                }

            }
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