package com.example.koratime.registration.createAccount

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addUserToFirestore
import com.example.koratime.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterViewModel : BasicViewModel<RegisterNavigator>() {
    override val TAG: String
        get() = RegisterViewModel::class.java.simpleName

    val firstName = ObservableField<String>()
    val firstNameError = ObservableField<String>()

    val secondName = ObservableField<String>()
    val secondNameError = ObservableField<String>()

    val userName = ObservableField<String>()
    val userNameError = ObservableField<String>()

    val email = ObservableField<String>()
    val emailError = ObservableField<String>()

    val password = ObservableField<String>()
    val passwordError = ObservableField<String>()

    val nationalID = ObservableField<String>()
    val nationalIDError = ObservableField<String>()

    val asManagerRadioButton = ObservableField<Boolean>()

    val imageUrl = MutableLiveData<String>()
    val imagesUri = MutableLiveData<Uri>()

    private val auth = Firebase.auth

    val showNationalID = MutableLiveData<Boolean>().apply { value = false }
    val toastMessage = MutableLiveData<String>()


    fun createAccount() {
        //validation
        if (validation()) {
            //create account in firebase
            addAccountToFirebase()
        }
    }
    fun openLoginActivity(){
        navigator?.openLoginActivity()
    }
    fun openImagePicker() {

    }


    private fun addAccountToFirebase() {
        showLoading.value = true
        auth.createUserWithEmailAndPassword(email.get()!!, password.get()!!)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    showLoading.value = false
                    toastMessage.value = task.exception?.localizedMessage
                    Log.e("Firebase: ", task.exception?.localizedMessage.toString())
                } else {
                    Log.e("Firebase: ", "Account added successfully to firestore")
                    if (imagesUri.value != null) {
                        uploadImageToStorage(task.result.user?.uid)
                    }else{
                        addUser(task.result.user?.uid)
                    }
                }

            }
    }

    private fun addUser(uid: String?) {
        val user = UserModel(
            id = uid,
            firstName = firstName.get(),
            secondName = secondName.get(),
            userName = userName.get(),
            nationalID = nationalID.get(),
            email = email.get(),
            profilePicture = imageUrl.value
        )
        addUserToFirestore(
            user,
            //OnSuccessListener
            {
                showLoading.value = false
                Log.e("Firebase: ", "account added to firestore")
                navigator?.closeActivity()
            },
            //OnFailureListener
            {
                showLoading.value = false
                Log.e("Firebase: ", "error adding account to firestore")
            })
    }

    private fun uploadImageToStorage(uid: String?) {
        showLoading.value = true
        com.example.koratime.database.uploadImageToStorage(
            imagesUri.value!!,
            onSuccessListener = { downloadUri ->
                log("Image uploaded successfully to Firebase Storage")
                // pass imageUrl to view model
                imageUrl.value = downloadUri.toString()
                addUser(uid)
            },
            onFailureListener = {
                showLoading.value = false
                log("Error uploading image to Firebase Storage $it")
            }
        )
    }
    private fun validation(): Boolean {
        var valid = true
        // Validate National ID if the user selected "Sign Up As Stadium Manager"
        if (asManagerRadioButton.get() == true && nationalID.get().isNullOrBlank()) {
            valid = false
            nationalIDError.set("Enter National ID")
        } else if (asManagerRadioButton.get() == true && nationalID.get()?.length != 14) {
            nationalIDError.set("Not Correct National ID")
            valid = false
        } else {
            nationalIDError.set(null)
        }

        //firstName
        if (firstName.get().isNullOrBlank()) {
            valid = false
            firstNameError.set("Enter First Name")
        } else {
            firstNameError.set(null)
        }
        //secondName
        if (secondName.get().isNullOrBlank()) {
            valid = false
            secondNameError.set("Enter Second Name")
        } else {
            secondNameError.set(null)
        }
        //userName
        if (userName.get().isNullOrBlank()) {
            valid = false
            userNameError.set("Enter User Name")
        } else {
            userNameError.set(null)
        }

        //email
        if (email.get().isNullOrBlank()) {
            valid = false
            emailError.set("Enter Email")
        } else {
            emailError.set(null)
        }

        //password
        if (password.get().isNullOrBlank()) {
            valid = false
            passwordError.set("Enter Password")
        } else if (password.get()?.length!! < 8) {
            valid = false
            passwordError.set("Password is less than 8")
        } else {
            passwordError.set(null)
        }



        return valid
    }
}