package com.example.koratime.model

data class FriendRequestModel(
    var requestID : String?=null,
    val senderID : String?=null,
    val senderName : String?=null,
    val receiverID : String?=null,
    val receiverName : String?=null,
    val senderProfilePicture : String?=null,
    val receiverProfilePicture : String?=null,
    val friendList:MutableList<String>?=null,

    val status : String?=null,
    val sentAt : Long?= System.currentTimeMillis()
){
    companion object{
        const val SUB_COLLECTION_REQUEST = "Requests"
    }
}
