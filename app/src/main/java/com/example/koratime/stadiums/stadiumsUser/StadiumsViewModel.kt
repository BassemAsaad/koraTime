package com.example.koratime.stadiums.stadiumsUser

import com.example.koratime.basic.BasicViewModel
import com.example.koratime.chat.ChatViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class StadiumsViewModel : BasicViewModel<StadiumsNavigator>() {
    override val TAG: String
        get() = StadiumsViewModel::class.java.simpleName
    fun logOut() {
        Firebase.auth.signOut()
        navigator?.Logout()
    }
}