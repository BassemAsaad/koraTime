package com.example.koratime.stadiums_manager.createStadium

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addStadiumToFirestore
import com.example.koratime.model.StadiumModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class AddStadiumViewModel : BasicViewModel<AddStadiumNavigator>() {

    val stadiumName = ObservableField<String>()
    val stadiumNameError = ObservableField<String>()
    val description = ObservableField<String>()
    val descriptionError = ObservableField<String>()
    val imageError = ObservableField<String>()

    val imageUrl = MutableLiveData<String>()
    private val user = Firebase.auth.currentUser

    fun createStadium(){
        if (validate()){
            val stadium= StadiumModel(
                stadiumName = stadiumName.get(),
                stadiumDescription = description.get(),
                userManager = user?.uid,
                stadiumImageUrl = imageUrl.value
            )
            //add in firebase
            addStadium(stadium)
            navigator?.closeActivity()
        }
    }

    private fun addStadium(stadium: StadiumModel){
        showLoading.value=true
        addStadiumToFirestore(
            stadium,
            onSuccessListener = {
                showLoading.value=false
                Log.e("Firebase","Stadium Added to Firestore")
                //navigate
            },
            onFailureListener = {
                showLoading.value=false
                messageLiveData.value = it.localizedMessage

            }

        )

    }



    private fun validate():Boolean{
        var valid = true
        if (stadiumName.get().isNullOrBlank()){
            stadiumNameError.set("Please enter the stadium name")
            valid=false
        } else{
            stadiumNameError.set(null)
        }
        if (description.get().isNullOrBlank()){
            descriptionError.set("Please enter the stadium name")
            valid=false
        } else{
            descriptionError.set(null)
        }

        if ( imageUrl.value==null){
            imageError.set("Set Image Error")
            valid = false
        }else{
            imageError.set(null)
        }

        return valid
    }

}