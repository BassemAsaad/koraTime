package com.example.koratime.model

data class FriendModel(
    val from : String?=null,
    val to : String?=null,
    val status : String?=null,
    val sentAt : Long?= System.currentTimeMillis()
){
    companion object{
        const val collectionName = "FriendRequest"
    }
}
