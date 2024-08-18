package com.example.koratime.chat

import com.example.koratime.model.FriendModel

interface ChatNavigator {
    fun openChatFriendsActivity(user: FriendModel?)
}