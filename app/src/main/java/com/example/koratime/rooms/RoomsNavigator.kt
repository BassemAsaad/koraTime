package com.example.koratime.rooms

import com.example.koratime.model.RoomModel

interface RoomsNavigator {
    fun openAddRoomActivity()
    fun openRoomChatActivity(room: RoomModel?, position: Int)
}