package com.example.koratime.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.koratime.R
import com.example.koratime.home.HomeActivity
import com.example.koratime.model.UserModel
import com.example.koratime.registration.login.LoginActivity
import com.example.koratime.utils.DataUtils
import com.example.koratime.utils.getUserFromFirestore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper())
            .postDelayed({
                checkLoggerUser()
            }, 3000)
    }
    private fun checkLoggerUser(){
        val firebaseUser = Firebase.auth.currentUser?.uid
        if (firebaseUser==null){
            startLoginScreen()
        }else{
            getUserFromFirestore(firebaseUser,
                onSuccessListener = {
                    val  user = it.toObject(UserModel::class.java)
                    DataUtils.user = user
                    startHomeActivity()
                },
                onFailureListener = {
                    startLoginScreen()
                }
            )

        }

    }
    private fun startLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}