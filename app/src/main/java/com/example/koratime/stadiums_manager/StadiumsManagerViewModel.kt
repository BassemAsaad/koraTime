package com.example.koratime.stadiums_manager

import com.example.koratime.basic.BasicViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class StadiumsManagerViewModel : BasicViewModel<StadiumsManagerNavigator>() {
    fun createStadium() {
        navigator?.createStadiumActivity()
    }

    fun logOut() {
        Firebase.auth.signOut()
        navigator?.Logout()
    }
}
