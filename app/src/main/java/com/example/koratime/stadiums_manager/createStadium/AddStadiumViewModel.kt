package com.example.koratime.stadiums_manager.createStadium

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.DataUtils
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

    val openingTime = MutableLiveData<Int>()
    val closingTime = MutableLiveData<Int>()

    val latitudeLiveData = MutableLiveData<Double>()
    val longitudeLiveData = MutableLiveData<Double>()
    val addressLiveData = MutableLiveData<String>()
    val locationError= ObservableField<String>()

    fun createStadium(){
        if (validate()){
            val stadium= StadiumModel(
                stadiumName = stadiumName.get(),
                stadiumDescription = description.get(),
                userManager = DataUtils.user!!.id,
                stadiumImageUrl = imageUrl.value,
                opening = openingTime.value,
                closing = closingTime.value,
                latitude = latitudeLiveData.value,
                longitude = longitudeLiveData.value,
                address = addressLiveData.value
            )
            Log.e("Firebase: ","address:  ${addressLiveData.value}")

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
            imageError.set("Add The Stadium Image")
            valid = false
        }else{
            imageError.set(null)
        }
        if (addressLiveData.value == null){
            locationError.set("Add The Stadium Location")
            valid = false
        } else{
            locationError.set(null)
        }

        return valid
    }

}