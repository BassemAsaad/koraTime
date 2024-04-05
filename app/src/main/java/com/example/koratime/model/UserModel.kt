package com.example.koratime.model

data class UserModel(
    val id : String?=null,
    val firstName:String?=null,
    val secondName:String?=null,
    val userName:String?=null,
    val email:String?=null
){
    companion object{
        const val collectionName = "User"
    }
}