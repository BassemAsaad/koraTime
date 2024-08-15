package com.example.koratime.chat

import com.example.koratime.basic.BasicViewModel

class ChatViewModel : BasicViewModel<ChatNavigator>(){
    override val TAG: String
        get() = ChatViewModel::class.java.simpleName
}