package com.example.koratime.registration.create_account

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.basic.Navigator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Register_ViewModel : BasicViewModel<Navigator>() {
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

    lateinit var auth: FirebaseAuth

    fun createAccount(){
        //validation
        if (validation()){
            //create account in firebase
            addAccount_toFirebase()
        }




    }

    fun addAccount_toFirebase() {
        showLoading.value =true
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email.get()!!, password.get()!!)
            .addOnCompleteListener {task->
                if (!task.isSuccessful){
                    Log.e("Firebase: ",task.exception?.localizedMessage.toString())
                } else{
                    Log.e("Firebase: ","Success")
                    navigator?.openHomeActivity()
                    messageLiveData.value = "Successful Login"
                    showLoading.value = false
                }
            }

    }

    fun validation():Boolean {
        var valid = true

        //firstName
        if (firstName.get().isNullOrBlank()){
            valid = false
            firstNameError.set("Enter First Name")
        } else{
            firstNameError.set(null)
        }
        //secondName
        if (secondName.get().isNullOrBlank()){
            valid = false
            secondNameError.set("Enter Second Name")
        } else {
            secondNameError.set(null)
        }
        //userName
        if (userName.get().isNullOrBlank() ){
            valid = false
            userNameError.set("Enter User Name")
        } else {
            userNameError.set(null)
        }
        //email
        if (email.get().isNullOrBlank()){
            valid = false
            emailError.set("Enter Email")
        } else {
            emailError.set(null)
        }
        //password
        if (password.get().isNullOrBlank() ){
            valid = false
            passwordError.set("Enter Password")
        }
        else if (password.get()?.length!! < 8){
            valid = false
            passwordError.set("Password is less than 8")
        }
        else {
            passwordError.set(null)
        }

        return valid
    }

}