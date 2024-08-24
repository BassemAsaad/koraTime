package com.example.koratime.rooms.roomChat

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.koratime.adapters.RoomMessagesAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.RoomMessageModel
import com.example.koratime.model.RoomModel
import com.example.koratime.utils.DataUtils
import com.example.koratime.utils.addRoomMessageToFirestore
import com.example.koratime.utils.getRoomMessagesFromFirestore
import com.google.firebase.firestore.DocumentChange
import java.util.Date

class RoomChatViewModel : BasicViewModel<RoomChatNavigator>() {
    override val TAG: String
        get() = RoomChatViewModel::class.java.simpleName

    var room: RoomModel? = null
    val messageAdapter = RoomMessagesAdapter()

    val messageField = ObservableField<String>()
    val messageFieldError = ObservableField<String>()

    val toastMessage = MutableLiveData<String>()

    fun adapterSetup() {
        listenForMessageUpdate()
    }

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

    private fun listenForMessageUpdate() {
        getRoomMessagesFromFirestore(room!!.roomID!!)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    log(error.localizedMessage!!)
                    toastMessage.value = "Error loading messages"
                } else {
                    val newMessageList = mutableListOf<RoomMessageModel?>()
                    for (dc in snapshots!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val message = dc.document.toObject(RoomMessageModel::class.java)
                                newMessageList.add(message)
                            }

                            DocumentChange.Type.MODIFIED -> log("Message Modified")
                            DocumentChange.Type.REMOVED -> log("Message Removed")
                        }
                    }
                    messageAdapter.changeData(newMessageList)
                    navigator?.scrollToBottom()
                }
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