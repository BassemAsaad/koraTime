package com.example.koratime.stadiums_user

import com.example.koratime.basic.BasicViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class StadiumsViewModel : BasicViewModel<StadiumNavigator>(){

    fun logOut(){
        Firebase.auth.signOut()
        navigator?.Logout()
    }
}