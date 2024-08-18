package com.example.koratime.rooms.stadiumRooms

import android.annotation.SuppressLint
import android.util.Log
import com.example.koratime.DataUtils
import com.example.koratime.adapters.PublicRoomsAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.getStadiumRoomFromFirestore
import com.example.koratime.model.RoomModel

class StadiumRoomsViewModel : BasicViewModel<StadiumRoomsNavigator>() {
    override val TAG: String
        get() = StadiumRoomsViewModel::class.java.simpleName
    val adapter = PublicRoomsAdapter(null)

    fun adapterSetup() {
        getStadiumRooms()
    }

    fun adapterCallback() {
        adapter.onItemClickListener = object : PublicRoomsAdapter.OnItemClickListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemClick(
                room: RoomModel?,
                position: Int,
                holder: PublicRoomsAdapter.ViewHolder
            ) {
                navigator?.openRoomChatActivity(room)
            }
        }
    }


    private fun getStadiumRooms() {
        getStadiumRoomFromFirestore(
            playerID = DataUtils.user!!.id!!,
            onSuccessListener = { querySnapShot ->
                val rooms = querySnapShot.toObjects(RoomModel::class.java)
                adapter.changeData(rooms)
                Log.e("Firebase ", " Adapter Working")

            }, onFailureListener = {
                Log.e("Firebase", " Error getting Rooms:", it)
            }
        )
    }
}