package com.example.koratime.registration.sign_in


import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.basic.Navigator
import com.google.firebase.*
import com.google.firebase.auth.*

class Login_ViewModel : BasicViewModel<Navigator>() {


    val email_login = ObservableField<String>()
    val emailError_login = ObservableField<String>()

    val password_login = ObservableField<String>()
    val passwordError_login = ObservableField<String>()

    lateinit var auth: FirebaseAuth


    fun login_account(){
        //validation
        if (validation()==true){
            signin_toFirebase()
        }


    }

     fun signin_toFirebase() {
         showLoading.value=true
         auth = Firebase.auth
         auth.signInWithEmailAndPassword(email_login.get()!!, password_login.get()!!)
             .addOnCompleteListener {task->
                 if (!task.isSuccessful){
                     Log.e("Firebase: ",task.exception?.localizedMessage.toString())
                 }else{
                     Log.e("Firebase: ", "Successful Sign in")
                     navigator?.openHomeActivity()
                     messageLiveData.value = "Successful Login"
                     showLoading.value = false
                 }
             }

    }

    fun validation():Boolean {
         var valid = true
         //email
         if (email_login.get().isNullOrBlank()){
             valid = false
             emailError_login.set("Enter Email")
         } else {
             emailError_login.set(null)
         }

         //password
         if (password_login.get().isNullOrBlank() ){
             valid = false
             passwordError_login.set("Enter Password")
         }
         else if (password_login.get()?.length!! < 8){
             valid = false
             passwordError_login.set("Password is less than 8")
         }
         else {
             passwordError_login.set(null)
         }

         return valid
    }


}