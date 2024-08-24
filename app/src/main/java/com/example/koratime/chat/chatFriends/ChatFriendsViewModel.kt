package com.example.koratime.chat.chatFriends

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.utils.DataUtils
import com.example.koratime.adapters.FriendMessagesAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.utils.addFriendMessageToFirestore
import com.example.koratime.utils.getFriendMessagesFromFirestore
import com.example.koratime.model.FriendMessageModel
import com.example.koratime.model.FriendModel
import com.google.firebase.firestore.DocumentChange
import java.util.Date

class ChatFriendsViewModel : BasicViewModel<ChatFriendsNavigator>() {
    override val TAG: String
        get() = ChatFriendsViewModel::class.java.simpleName
    var friend: FriendModel? = null
    val messageAdapter = FriendMessagesAdapter()
    val messageField = ObservableField<String>()
    val messageFieldError = ObservableField<String>()

    val toastMessage = MutableLiveData<String>()

    fun listenForMessageUpdate() {
        getFriendMessagesFromFirestore(
            senderID = DataUtils.user!!.id!!,
            friendshipID = friend!!.friendshipID!!,
        ).addSnapshotListener { snapshots, error ->
            if (error != null) {
                toastMessage.value = "Error"
            } else {
                val newMessageList = mutableListOf<FriendMessageModel?>()
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val message = dc.document.toObject(FriendMessageModel::class.java)
                        newMessageList.add(message)
                    }
                }
                messageAdapter.changeData(newMessageList)
                navigator?.scrollToBottom() // Scroll to the last position

            }
        }
    }

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