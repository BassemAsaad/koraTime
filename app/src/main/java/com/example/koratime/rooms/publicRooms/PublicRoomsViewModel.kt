package com.example.koratime.rooms.publicRooms

import androidx.lifecycle.MutableLiveData
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.rooms.publicRooms.PublicRoomsNavigator

class PublicRoomsViewModel : BasicViewModel<PublicRoomsNavigator>() {

    val password = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()
    val roomPassword = MutableLiveData<String>()

    fun checkRoomPassword():Boolean{
        var check = true
        if (password.value != roomPassword.value){
            check = false
            passwordError.value=("Wrong Password")
        }else {
            passwordError.value=(null)
        }
        return check
    }




}