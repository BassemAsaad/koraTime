package com.example.koratime.rooms.createRoom

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
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
    val password = ObservableField<String>()
    val imageUrl = MutableLiveData<String>()
    private val user = Firebase.auth.currentUser

    val toastMessage = MutableLiveData<String>()

    fun createRoom(){
        if (validate()){
            val room = RoomModel(
                    name = roomName.get(),
                    description = description.get(),
                    password = password.get(),
                    imageUrl = imageUrl.value,
                    userManager = user?.uid
            )
            //add in firebase
            addRoom(room)
            navigator?.roomsFragment()
        }
    }
    private fun addRoom(room: RoomModel) {
        showLoading.value=true
        addRoomToFirestore(
            room,
            onSuccessListener = {
                showLoading.value=false
                Log.e("Firebase","Room Added to Firestore")
                //navigate
                navigator?.roomsFragment()
            },
            onFailureListener = {
                showLoading.value=false
                toastMessage.value = it.localizedMessage
            }
        )
    }
    private fun validate():Boolean{
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