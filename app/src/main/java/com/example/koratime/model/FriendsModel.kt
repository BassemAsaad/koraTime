package com.example.koratime.model

data class FriendsModel(
    val from : String?=null,
    val to : String?=null,
    val status : String?=null,
    val id : String?=null,
    var sentAt : Long?=null,
){
    companion object{
        const val collectionName = "sendFriendRequest"
    }
}
