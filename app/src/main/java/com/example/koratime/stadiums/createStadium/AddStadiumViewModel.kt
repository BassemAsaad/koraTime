package com.example.koratime.stadiums.createStadium

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.basic.BasicViewModel

class AddStadiumViewModel : BasicViewModel<AddStadiumNavigator>(){

    val stadiumName = ObservableField<String>()
    val stadiumNameError = ObservableField<String>()
    val stadiumDescription = ObservableField<String>()
    val stadiumDescriptionError = ObservableField<String>()
    val imageUrl = MutableLiveData<String>()
















    fun validate():Boolean{
        var valid = true
        if (stadiumName.get().isNullOrBlank()){
            stadiumNameError.set("Please enter the room name")
            valid=false
        } else{
            stadiumNameError.set(null)
        }
        if (stadiumDescription.get().isNullOrBlank()){
            stadiumDescriptionError.set("Please enter the room name")
            valid=false
        } else{
            stadiumDescriptionError.set(null)
        }
        return valid
    }

}