package com.example.koratime.model

data class AddFriendModel(
    val from : String?=null,
    val to : String?=null,
    val status : String?=null,
    var sentAt : Long?= System.currentTimeMillis()
){
    companion object{
        const val collectionName = "FriendRequest"
    }
}
