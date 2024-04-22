package com.example.koratime.rooms

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.RoomModel

class RoomsViewModel : BasicViewModel<RoomsNavigator>() {

    val password = ObservableField<String>()
    val passwordError = ObservableField<String>()
    val roomPassword = MutableLiveData<String>()

    fun checkRoomPassword():Boolean{
        var check = true
        if (password.get() != roomPassword.value){
            passwordError.set("Wrong Password")
            check = false
        }else {
            passwordError.set(null)
        }
        return check
    }


    fun createRoom(){
        navigator?.openAddRoomActivity()
    }

}