package com.example.koratime.rooms

import com.example.koratime.basic.BasicViewModel

class RoomsViewModel : BasicViewModel<RoomsNavigator>() {
    fun createRoom(){
        navigator?.openAddRoomActivity()
    }

}