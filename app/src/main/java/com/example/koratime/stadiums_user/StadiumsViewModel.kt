package com.example.koratime.stadiums_user

import com.example.koratime.basic.BasicViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class StadiumsViewModel : BasicViewModel<StadiumsNavigator>() {

    fun logOut() {
        Firebase.auth.signOut()
        navigator?.Logout()
    }
}