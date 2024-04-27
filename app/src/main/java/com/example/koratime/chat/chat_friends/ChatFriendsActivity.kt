package com.example.koratime.chat.chat_friends

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityChatFriendsBinding

class ChatFriendsActivity : BasicActivity<ActivityChatFriendsBinding,ChatFriendsViewModel>() {

    override fun getLayoutID(): Int {
        return R.layout.activity_chat_friends
    }

    override fun initViewModel(): ChatFriendsViewModel {
        return ViewModelProvider(this)[ChatFriendsViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()


    }

    override fun initView() {

    }
}