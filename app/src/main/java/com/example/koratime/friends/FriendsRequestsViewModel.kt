package com.example.koratime.friends

import com.example.koratime.basic.BasicViewModel
import com.example.koratime.chat.ChatViewModel

class FriendsRequestsViewModel : BasicViewModel<FriendsRequestsNavigator>() {
    override val TAG: String
        get() = FriendsRequestsViewModel::class.java.simpleName
    fun openSearch() {
        navigator?.openSearchActivity()
    }


}