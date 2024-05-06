package com.example.koratime.registration.log_in


import android.util.Log
import androidx.databinding.ObservableField
import com.example.koratime.DataUtils
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.getUserFromFirestore
import com.example.koratime.model.UserModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.*
import com.google.firebase.auth.*

class LoginViewModel : BasicViewModel<LoginNavigator>() {

    val emailLogin = ObservableField<String>()
    val emailErrorLogin = ObservableField<String>()

    val passwordLogin = ObservableField<String>()
    val passwordErrorLogin = ObservableField<String>()

    private lateinit var auth: FirebaseAuth


    fun loginIntoAccount(){
        //validation
        if (validation()){
            loginWithFirebase()
        }


    }


    private fun loginWithFirebase() {
        showLoading.value=true
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(emailLogin.get()!!, passwordLogin.get()!!)
            .addOnCompleteListener {task->
                if (!task.isSuccessful){
                    Log.e("Firebase: ",task.exception?.localizedMessage.toString())
                    showLoading.value = false
                    messageLiveData.value = "Invalid Email or Password"
                }else{

                    Log.e("Firebase: ", "Successful Login")
                    messageLiveData.value = "Successful Login"
                    getUserFromFirestore(task.result.user?.uid)
                }
            }

    }

    private fun getUserFromFirestore(uid: String?) {
        getUserFromFirestore(
            uid,
            //OnSuccessListener
            onSuccessListener = OnSuccessListener{ docSnapshot->
                val user = docSnapshot.toObject(UserModel::class.java)
                if (user == null){
                    messageLiveData.value = "Invalid Email or Password"
                    Log.e("Firebase: ", "Not a successful Login")
                    showLoading.value = false
                    return@OnSuccessListener

                }else{
                    showLoading.value = false
                    Log.e("Firebase: ", "Successful Login")
                    DataUtils.user = user
                    if (user.nationalID==null){
                        navigator?.openHomeActivity()
                    } else {
                        navigator?.openHomeManagerActivity()
                    }
                }
            }//end OnSuccessListener
            ,
            onFailureListener = {
                messageLiveData.value = it.localizedMessage
            }//end OnFailureListener
        )
    }

    private fun validation():Boolean {
        var valid = true
        //email
        if (emailLogin.get().isNullOrBlank()){
            valid = false
            emailErrorLogin.set("Enter Email")
        } else {
            emailErrorLogin.set(null)
        }

        //password
        if (passwordLogin.get().isNullOrBlank() ){
            valid = false
            passwordErrorLogin.set("Enter Password")
        }
        else if (passwordLogin.get()?.length!! < 8){
            valid = false
            passwordErrorLogin.set("Password is less than 8")
        }
        else {
            passwordErrorLogin.set(null)
        }

        return valid
    }


}