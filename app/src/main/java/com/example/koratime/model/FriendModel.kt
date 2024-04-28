package com.example.koratime.model

data class FriendModel(
    var requestID : String?=null,
    val sender : String?=null,
    val receiver : String?=null,
    val status : String?=null,
    val sentAt : Long?= System.currentTimeMillis()
){
    companion object{
        const val COLLECTION_NAME_SENDER = "AddedFriends"
        const val COLLECTION_NAME_RECEIVER = "PendingFriends"
    }
}
