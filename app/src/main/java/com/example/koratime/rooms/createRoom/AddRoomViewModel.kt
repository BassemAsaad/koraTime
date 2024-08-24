package com.example.koratime.rooms.createRoom

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.utils.addRoomToFirestore
import com.example.koratime.utils.uploadImageToStorage
import com.example.koratime.model.RoomModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class AddRoomViewModel : BasicViewModel<AddRoomNavigator>() {
    override val TAG: String
        get() = AddRoomViewModel::class.java.simpleName

    val roomName = ObservableField<String>()
    val roomNameError = ObservableField<String>()

    val description = ObservableField<String>()
    val descriptionError = ObservableField<String>()

    val password = ObservableField<String>()

    val imageUrl = MutableLiveData<String>()
    val imagesUri = MutableLiveData<Uri>()

    private val user = Firebase.auth.currentUser

    val toastMessage = MutableLiveData<String>()

    fun openImagePicker() {
        navigator?.openImagePicker()
    }

    fun createRoom() {
        if (validate()) {
            //add in firebase
            if (imagesUri.value != null) {
                uploadImageToStorage()
            } else {
                addRoom()
            }
        }
    }

    private fun addRoom() {
        val room = RoomModel(
            name = roomName.get(),
            description = description.get(),
            password = password.get(),
            imageUrl = imageUrl.value,
            userManager = user?.uid
        )
        addRoomToFirestore(
            room,
            onSuccessListener = {
                showLoading.value = false
                Log.e("Firebase", "Room Added to Firestore")
                //navigate
                navigator?.closeActivity()
            },
            onFailureListener = {
                showLoading.value = false
                toastMessage.value = it.localizedMessage
            }
        )
    }

    private fun uploadImageToStorage() {
        showLoading.value = true
        uploadImageToStorage(
            imagesUri.value,
            onSuccessListener = { downloadUri ->
                log("Firebase Storage: Image uploaded successfully")
                imageUrl.value = downloadUri.toString()
                addRoom()

            },
            onFailureListener = {
                log("Firebase Storage: Error uploading image $it")
                showLoading.value = false
            }
        )
    }

    private fun validate(): Boolean {
        var valid = true
        if (roomName.get().isNullOrBlank()) {
            roomNameError.set("Please enter the room name")
            valid = false
        } else {
            roomNameError.set(null)
        }
        if (description.get().isNullOrBlank()) {
            descriptionError.set("Please enter the room name")
            valid = false
        } else {
            descriptionError.set(null)
        }

        return valid
    }


}