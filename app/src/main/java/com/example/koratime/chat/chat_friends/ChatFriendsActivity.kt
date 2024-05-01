package com.example.koratime.chat.chat_friends

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.adapters.FriendMessagesAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityChatFriendsBinding
import com.example.koratime.model.FriendModel

@Suppress("DEPRECATION")
class ChatFriendsActivity : BasicActivity<ActivityChatFriendsBinding,ChatFriendsViewModel>(),ChatFriendsNavigator {
    lateinit var friendModel : FriendModel
    private val messageAdapter = FriendMessagesAdapter()
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
        dataBinding.vm = viewModel
        viewModel.navigator = this

        friendModel = intent.getParcelableExtra(Constants.FRIEND)!!
        viewModel.friend = friendModel

        setSupportActionBar(dataBinding.toolbar)
        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Enable title on Toolbar
        supportActionBar?.title = friendModel.friendName
        supportActionBar?.setDisplayShowTitleEnabled(true)

        dataBinding.recyclerView.adapter=messageAdapter
    }
    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }
    override fun onStart() {
        super.onStart()

    }
}