package com.example.koratime.friends

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.koratime.DataUtils
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.FriendModel
import com.example.koratime.model.FriendRequestModel

class FriendsRequestsViewModel : BasicViewModel<FriendsRequestsNavigator>() {
    fun openSearch(){
        navigator?.openSearchActivity()
    }


}