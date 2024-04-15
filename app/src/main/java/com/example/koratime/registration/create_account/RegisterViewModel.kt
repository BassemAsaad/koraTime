package com.example.koratime.registration.create_account

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addUser_toFirestore
import com.example.koratime.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterViewModel : BasicViewModel<RegisterNavigator>() {
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

    val asManagerRadioButton = ObservableField<Boolean>()
    val radioButtonsError = ObservableField<String>()

    val nationalID = ObservableField<String>()
    val nationalIDError = ObservableField<String>()

    private val auth = Firebase.auth

    val showNationalID = MutableLiveData<Boolean>().apply { value = false }









    fun createAccount(){
        //validation
        if (validation()){
            //create account in firebase
            addAccount_toFirebase()
        }
    }


    private fun addAccount_toFirebase() {
        showLoading.value = true
        auth.createUserWithEmailAndPassword(email.get()!!, password.get()!!)
            .addOnCompleteListener{task ->
                if (!task.isSuccessful){
                    showLoading.value = false
                    Log.e("Firebase: ",task.exception?.localizedMessage.toString())
                } else{
                    showLoading.value = false
                    Log.e("Firebase: ", "account added to firestore")
                    createFirestore_User(task.result.user?.uid)
                }

            }
    }

    private fun createFirestore_User(uid: String?) {
        val user = UserModel(
            id = uid,
            firstName = firstName.get(),
            secondName = secondName.get(),
            userName = userName.get(),
            nationalID= nationalID.get(),
            email = email.get()
        )
        addUser_toFirestore(
            user,
            //OnSuccessListener
            {
                Log.e("Firebase: ", "account added to firestore")
                messageLiveData.value = "Successful Register"
            },
            //OnFailureListener
            {
                showLoading.value=false
                messageLiveData.value = it.localizedMessage
            })
    }


    fun validation():Boolean {
        var valid = true



        // Validate National ID if the user selected "Sign Up As Stadium Manager"
    if (asManagerRadioButton.get()==true && nationalID.get().isNullOrBlank()){
        valid = false
        nationalIDError.set("Enter National ID")
    } else if (asManagerRadioButton.get()==true && nationalID.get()?.length !=14)  {
        nationalIDError.set("Not Correct National ID")
    } else{
        nationalIDError.set(null)
    }

        //firstName
        if (firstName.get().isNullOrBlank()){
            valid=false
            firstNameError.set("Enter First Name")
        } else{
            firstNameError.set(null)
        }
        //secondName
        if (secondName.get().isNullOrBlank()){
            valid=false
            secondNameError.set("Enter Second Name")
        } else {
            secondNameError.set(null)
        }
        //userName
        if (userName.get().isNullOrBlank() ){
            valid=false
            userNameError.set("Enter User Name")
        } else {
            userNameError.set(null)
        }

        //email
        if (email.get().isNullOrBlank()){
            valid=false
            emailError.set("Enter Email")
        } else {
            emailError.set(null)
        }

        //password
        if (password.get().isNullOrBlank() ){
            valid=false
            passwordError.set("Enter Password")
        }
        else if (password.get()?.length!! < 8){
            valid=false
            passwordError.set("Password is less than 8")
        }
        else {
            passwordError.set(null)
        }



        return valid
    }
}