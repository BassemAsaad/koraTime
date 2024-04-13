package com.example.koratime.registration.sign_in


import android.util.Log
import androidx.databinding.ObservableField
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.getUserData_forLogin
import com.example.koratime.model.UserModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.*
import com.google.firebase.auth.*

class LoginViewModel : BasicViewModel<LoginNavigator>() {

    val email_login = ObservableField<String>()
    val emailError_login = ObservableField<String>()

    val password_login = ObservableField<String>()
    val passwordError_login = ObservableField<String>()

    lateinit var auth: FirebaseAuth


    fun login_account(){
        //validation
        if (validation()==true){
            login_withFirebase()
        }


    }

    fun createAccount(){
        navigator?.openRegisterActivity()
    }

    fun login_withFirebase() {
        showLoading.value=true
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email_login.get()!!, password_login.get()!!)
            .addOnCompleteListener {task->
                if (!task.isSuccessful){
                    Log.e("Firebase: ",task.exception?.localizedMessage.toString())
                    showLoading.value = false
                    messageLiveData.value = "Invalid Email or Password"
                }else{

                    Log.e("Firebase: ", "Successful")

                    showLoading.value = false
                    messageLiveData.value = "Successful Login"
                    getUser_fromFirestore(task.result.user?.uid)
                }
            }

    }

    private fun getUser_fromFirestore(uid: String?) {
        showLoading.value=true
        getUserData_forLogin(
            uid,
            //OnSuccessListener
            OnSuccessListener{docSnapshot->
                showLoading.value=false
                val user = docSnapshot.toObject(UserModel::class.java)
                if (user == null){
                    messageLiveData.value = "Invalid Email or Password"
                    return@OnSuccessListener

                }else{
                    showLoading.value=false
                    navigator?.openHomeActivity()
                }
            }//end OnSuccessListener
            ,
            OnFailureListener{
                showLoading.value=false
                messageLiveData.value = it.localizedMessage
            }//end OnFailureListener
        )
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