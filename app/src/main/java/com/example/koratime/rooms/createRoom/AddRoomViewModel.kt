package com.example.koratime.rooms.createRoom

import androidx.databinding.ObservableField
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addRoomToFirestore
import com.example.koratime.model.RoomModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class AddRoomViewModel : BasicViewModel<AddRoomNavigator>() {

    val roomName = ObservableField<String>()
    val roomNameError = ObservableField<String>()
    val description = ObservableField<String>()
    val descriptionError = ObservableField<String>()
    val imageUrl = ObservableField<String>()

    private val user = Firebase.auth.currentUser
    fun createRoom(){
        if (validate()){
            val room = RoomModel(
                name = roomName.get(),
                description = description.get(),
                imageUrl = imageUrl.get(),
                userManager = user?.uid
            )
            //add in firebase
            addRoom(room)
        }
    }
    private fun addRoom(room: RoomModel) {
        showLoading.value=true
        addRoomToFirestore(
            room,
            onSuccessListener = {
                showLoading.value=false

                //navigate
                navigator?.roomActivity()
            },
            onFailureListener = {
                showLoading.value=false
                messageLiveData.value = it.localizedMessage
            }
        )
    }
    fun validate():Boolean{
        var valid = true
        if (roomName.get().isNullOrBlank()){
            roomNameError.set("Please enter the room name")
            valid=false
        } else{
            roomNameError.set(null)
        }
        if (description.get().isNullOrBlank()){
            descriptionError.set("Please enter the room name")
            valid=false
        } else{
            descriptionError.set(null)
        }

        return valid
    }
}