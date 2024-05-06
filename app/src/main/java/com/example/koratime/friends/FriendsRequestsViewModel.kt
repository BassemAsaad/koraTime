package com.example.koratime.friends

import com.example.koratime.basic.BasicViewModel

class FriendsRequestsViewModel : BasicViewModel<FriendsRequestsNavigator>() {

    fun openSearch(){
        navigator?.openSearchActivity()
    }
}