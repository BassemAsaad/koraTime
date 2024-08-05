package com.example.koratime.rooms.room_chat

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.DataUtils
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addRoomMessageToFirestore
import com.example.koratime.model.RoomMessageModel
import com.example.koratime.model.RoomModel
import java.util.Date

class RoomChatViewModel : BasicViewModel<RoomChatNavigator>() {
    val messageField = ObservableField<String>()
    val messageFieldError = ObservableField<String>()

    var room: RoomModel? = null
    val toastMessage = MutableLiveData<String>()

    fun sendMessage() {
        val roomMessageModel = RoomMessageModel(
            content = messageField.get(),
            roomID = room?.roomID,
            senderID = DataUtils.user?.id,
            senderName = DataUtils.user?.userName,
            dateTime = Date().time
        )

        if (validation()) {
            addRoomMessageToFirestore(
                message = roomMessageModel,
                onSuccessListener = {
                    Log.e("Firebase", "message sent successfully")
                    messageField.set("")

                },
                onFailureListener = {
                    toastMessage.value = "message was not sent"
                }
            )
        }


    }

    private fun validation(): Boolean {
        var check = true

        if (messageField.get().isNullOrBlank()) {
            check = false
            messageFieldError.set("Cant send empty message")
        } else {
            messageFieldError.set(null)
        }

        return check
    }
}