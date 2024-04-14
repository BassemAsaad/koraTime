package com.example.koratime.stadiums.createStadium

import androidx.databinding.ObservableField
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addRoomToFirestore
import com.example.koratime.database.addStadiumToFirestore
import com.example.koratime.model.RoomModel
import com.example.koratime.model.StadiumModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class AddStadiumViewModel : BasicViewModel<AddStadiumNavigator>(){

    val stadiumName = ObservableField<String>()
    val stadiumNameError = ObservableField<String>()
    val description = ObservableField<String>()
    val descriptionError = ObservableField<String>()
    val imageUrl = ObservableField<String>()

    private val user = Firebase.auth.currentUser
    fun createStadium(){
        if (validate()){
            val stadium = StadiumModel(
                stadiumName = stadiumName.get(),
                stadiumDescription = description.get(),
                imageUrl = imageUrl.get(),
                userManager = user?.uid
            )
            //add in firebase
            addStadium(stadium)
        }
    }
    private fun addStadium(stadiumModel: StadiumModel) {
        showLoading.value=true

        addStadiumToFirestore(
            stadiumModel,
            onSuccessListener = {
                showLoading.value=false

                //navigate
                navigator?.stadiumFragment()
            },
            onFailureListener = {
                showLoading.value=false
                messageLiveData.value = it.localizedMessage
            }
        )



    }
    fun validate():Boolean{
        var valid = true
        if (stadiumName.get().isNullOrBlank()){
            stadiumNameError.set("Please enter the room name")
            valid=false
        } else{
            stadiumNameError.set(null)
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