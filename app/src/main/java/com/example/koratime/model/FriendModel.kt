package com.example.koratime.model

data class FriendModel(
    var friendshipID: String?=null,
    val friendID : String?=null,
    val friendPicture : String?=null,
    val friendName : String?=null,
){
    companion object{
        const val COLLECTION_NAME = "Friend"
    }
}
