package com.example.koratime.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MessageModel(
    var messageID: String?=null,
    val content: String?=null,
    val senderName: String?=null,
    val senderID: String?=null,
    val dateTime: Long?=null,
    val roomID: String?=null,
){
    companion object{
        const val COLLECTION_NAME = "Messages"
    }
    fun formatTime():String{
        val date = Date(dateTime!!)
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return simpleDateFormat.format(date)

    }
}
