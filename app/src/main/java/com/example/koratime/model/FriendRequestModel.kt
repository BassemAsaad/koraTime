package com.example.koratime.model

data class FriendRequestModel(
    var requestID : String?=null,
    val senderID : String?=null,
    val senderProfilePicture : String?=null,
    val senderName : String?=null,
    val receiverID : String?=null,
    val status : String?=null,
    val sentAt : Long?= System.currentTimeMillis()
){
    companion object{
        const val COLLECTION_NAME_SENDER = "AddedFriends"
        const val COLLECTION_NAME_RECEIVER = "PendingFriends"
    }
}
