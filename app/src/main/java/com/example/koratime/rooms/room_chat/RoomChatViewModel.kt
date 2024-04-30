package com.example.koratime.rooms.room_chat

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.DataUtils
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addMessageToFirestore
import com.example.koratime.model.MessageModel
import com.example.koratime.model.RoomModel
import java.util.Date

class RoomChatViewModel : BasicViewModel<RoomChatNavigator>() {
    val messageField = ObservableField<String>()
    var room : RoomModel?=null
    val toastLiveData = MutableLiveData<String>()

    fun sendMessage(){
        val messageModel = MessageModel(
            content = messageField.get(),
            roomID = room?.id,
            senderID = DataUtils.user?.id,
            senderName =DataUtils.user?.userName,
            dateTime = Date().time
        )

        addMessageToFirestore(messageModel,
            onSuccessListener = {
                Log.e("Firebase","message sent successfully")
                messageField.set("")

            },
            onFailureListener = {
                toastLiveData.value = "message was not sent"
            }
        )

    }
}