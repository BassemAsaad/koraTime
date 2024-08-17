package com.example.koratime.stadiums.createStadium

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.DataUtils
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.chat.ChatViewModel
import com.example.koratime.database.addStadiumToFirestore
import com.example.koratime.database.uploadImageToStorage
import com.example.koratime.model.StadiumModel


class AddStadiumViewModel : BasicViewModel<AddStadiumNavigator>() {
    override val TAG: String
        get() = AddStadiumViewModel::class.java.simpleName

    val stadiumName = ObservableField<String>()
    val stadiumNameError = ObservableField<String>()

    val description = ObservableField<String>()
    val descriptionError = ObservableField<String>()

    val number = ObservableField<String>()
    val numberError = ObservableField<String>()

    val price = ObservableField<String>()
    val priceError = ObservableField<String>()

    val imageError = ObservableField<String>()
    val imagesUri = MutableLiveData<Uri>()

    val openingTime = MutableLiveData<Int>()
    val closingTime = MutableLiveData<Int>()

    val latitudeLiveData = MutableLiveData<Double>()
    val longitudeLiveData = MutableLiveData<Double>()
    val addressLiveData = MutableLiveData<String>()
    val locationError = ObservableField<String>()

    val toastMessage = MutableLiveData<String>()


    fun createStadium() {

        if (validate()) {
            Log.e("Firebase: ", "address:  ${addressLiveData.value}")
            //add in firebase
            uploadImageToStorage()

        }
    }

    private fun addStadium(downloadUri: String) {
        val stadium = StadiumModel(
            stadiumName = stadiumName.get(),
            stadiumDescription = description.get(),
            stadiumTelephoneNumber = number.get(),
            stadiumPrice = price.get(),
            userManager = DataUtils.user!!.id,
            stadiumImageUrl = downloadUri,
            opening = openingTime.value,
            closing = closingTime.value!! + openingTime.value!!,
            latitude = latitudeLiveData.value,
            longitude = longitudeLiveData.value,
            address = addressLiveData.value
        )
        addStadiumToFirestore(
            stadium,
            onSuccessListener = {
                showLoading.value = false
                Log.e("Firebase", "Stadium Added to Firestore")
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
            imagesUri.value!!,
            onSuccessListener = { downloadUri ->
                log("Image uploaded successfully to Firebase Storage")
                // pass imageUrl to view model

                addStadium(downloadUri.toString())
            },
            onFailureListener = {
                log("Error uploading image to Firebase Storage $it")
                showLoading.value = false
            }
        )

    }
    private fun validate(): Boolean {
        var valid = true
        if (stadiumName.get().isNullOrBlank()) {
            stadiumNameError.set("Please enter the stadium name")
            valid = false
        } else {
            stadiumNameError.set(null)
        }
        if (description.get().isNullOrBlank()) {
            descriptionError.set("Please enter the stadium name")
            valid = false
        } else {
            descriptionError.set(null)
        }
        if (number.get().isNullOrBlank()) {
            numberError.set("Please enter the stadium number")
            valid = false
        } else if (number.get()!!.length != 11) {
            valid = false
            numberError.set("stadium telephone number is wrong")
        } else {
            numberError.set(null)
        }
        if (price.get().isNullOrBlank()) {
            priceError.set("Please enter the stadium price per hour")
            valid = false
        } else if (price.get()!!.length > 3) {
            valid = false
            priceError.set("stadium price should be realistic")
        } else {
            priceError.set(null)
        }
        if (imagesUri.value == null) {
            imageError.set("Add The Stadium Image")
            valid = false
        } else {
            imageError.set(null)
        }
        if (addressLiveData.value == null) {
            locationError.set("Add The Stadium Location")
            valid = false
        } else {
            locationError.set(null)
        }

        return valid
    }

}