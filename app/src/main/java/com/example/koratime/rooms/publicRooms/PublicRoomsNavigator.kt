package com.example.koratime.rooms.publicRooms

import com.example.koratime.model.RoomModel

interface PublicRoomsNavigator {
    fun openRoomChatActivity(room: RoomModel?)
}