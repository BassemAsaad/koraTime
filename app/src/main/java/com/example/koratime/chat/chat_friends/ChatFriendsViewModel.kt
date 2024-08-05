package com.example.koratime.chat.chat_friends

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.DataUtils
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addFriendMessageToFirestore
import com.example.koratime.model.FriendMessageModel
import com.example.koratime.model.FriendModel
import java.util.Date

class ChatFriendsViewModel : BasicViewModel<ChatFriendsNavigator>() {
    val messageField = ObservableField<String>()
    val messageFieldError = ObservableField<String>()
    var friend: FriendModel? = null
    val toastMessage = MutableLiveData<String>()
    fun sendMessage() {
        val friendMessageModel = FriendMessageModel(
            content = messageField.get(),
            senderID = DataUtils.user!!.id,
            receiverID = friend!!.friendID,
            senderName = friend!!.friendName,
            dateTime = Date().time
        )
        if (validation()) {
            addFriendMessageToFirestore(
                message = friendMessageModel,
                friendshipID = friend!!.friendshipID!!,
                onSuccessListener = {
                    Log.e("Firebase", " Message sent successfully")
                    messageField.set("")
                },
                onFailureListener = {
                    toastMessage.value = "Message was not sent "
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