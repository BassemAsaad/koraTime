package com.example.koratime.stadiums_manager.manageStadium

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addMultipleImagesToFirestore
import com.example.koratime.model.StadiumModel

class ManagingStadiumViewModel : BasicViewModel<ManagingStadiumNavigator>() {
    var stadium : StadiumModel?=null
    var listOfUrls = MutableLiveData<List<String>>()
    fun createListForOpeningTimes(openingIndex: Int, closingIndex: Int, timeSlotsArray: Array<String>): List<String> {
        val availableTimeSlots = mutableListOf<String>()

        // Ensure closing index is greater than opening index and within bounds
        if ((openingIndex >= 0) && (closingIndex >= openingIndex) && (closingIndex < timeSlotsArray.size)) {
            for (i in openingIndex..closingIndex) {
                availableTimeSlots.add(timeSlotsArray[i])
            }
        }

        return availableTimeSlots
    }

    fun addImageUrlsToFirestore(){
        addMultipleImagesToFirestore(
            listOfUrls.value!!.toMutableList(),
            stadium!!.stadiumID!!,
            onSuccessListener = {
                Log.e("Firebase","Images uploaded successfully to firestore")
                showLoading.value=false
            },
            onFailureListener = {
                showLoading.value=false
                Log.e("Firebase","Error uploading images to firestore")

            }
        )
    }
    fun removeBookedListFromOpeningTimes(allTimeSlots: List<String>, bookedTimeSlots: List<String>): List<String> {
        return allTimeSlots.filterNot { it in bookedTimeSlots }
    }
}