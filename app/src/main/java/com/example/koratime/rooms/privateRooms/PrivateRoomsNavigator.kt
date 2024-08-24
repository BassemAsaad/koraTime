package com.example.koratime.rooms.privateRooms

import com.example.koratime.model.RoomModel

interface PrivateRoomsNavigator {
    fun openAddRoomActivity()
    fun openRoomChatActivity(room: RoomModel?)
    fun openDialog(room: RoomModel?)
}