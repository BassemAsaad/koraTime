package com.example.koratime.stadiums.stadiumManager

import com.example.koratime.basic.BasicViewModel
import com.example.koratime.chat.ChatViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class StadiumsManagerViewModel : BasicViewModel<StadiumsManagerNavigator>() {
    override val TAG: String
        get() = StadiumsManagerViewModel::class.java.simpleName
    fun createStadium() {
        navigator?.createStadiumActivity()
    }

    fun logOut() {
        Firebase.auth.signOut()
        navigator?.Logout()
    }
}
