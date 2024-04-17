package com.example.koratime.friends_messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityFriendsChatBinding

class FriendsChatActivity : BasicActivity<ActivityFriendsChatBinding,FriendsChatViewModel>(),FriendsChatNavigator {


    override fun getLayoutID(): Int {
        return R.layout.activity_friends_chat
    }

    override fun initViewModel(): FriendsChatViewModel {
        return ViewModelProvider(this)[FriendsChatViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_chat)
    }

    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel

    }
}