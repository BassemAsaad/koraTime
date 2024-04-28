package com.example.koratime.rooms

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.RoomModel

class RoomsViewModel : BasicViewModel<RoomsNavigator>() {

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


    fun createRoom(){
        navigator?.openAddRoomActivity()
    }

}