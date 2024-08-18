package com.example.koratime.chat.chatFriends

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityChatFriendsBinding
import com.example.koratime.model.FriendModel

@Suppress("DEPRECATION")
class ChatFriendsActivity : BasicActivity<ActivityChatFriendsBinding, ChatFriendsViewModel>(),
    ChatFriendsNavigator {
    override val TAG: String
        get() = "ChatFriendsActivity"
    private lateinit var friendModel: FriendModel
    override fun getLayoutID(): Int {
        return R.layout.activity_chat_friends
    }

    override fun initViewModel(): ChatFriendsViewModel {
        return ViewModelProvider(this)[ChatFriendsViewModel::class.java]
    }

    override fun initView() {
        friendModel = intent.getParcelableExtra(Constants.FRIEND)!!
        callback()
    }

    override fun callback() {
        setSupportActionBar(dataBinding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = friendModel.friendName
        }
        viewModel.apply {
            navigator = this@ChatFriendsActivity
            friend = friendModel
            toastMessage.observe(this@ChatFriendsActivity) { message ->
                Toast.makeText(this@ChatFriendsActivity, message, Toast.LENGTH_SHORT).show()
            }
            listenForMessageUpdate()

        }
        dataBinding.apply {
            vm = viewModel
            val linearLayoutManager = LinearLayoutManager(this@ChatFriendsActivity)
            linearLayoutManager.stackFromEnd = true // This will start the layout from the end
            recyclerView.apply {
                layoutManager = linearLayoutManager
                adapter = viewModel.messageAdapter
            }
        }

    }

    
    override fun scrollToBottom() {
        dataBinding.recyclerView.postDelayed({
            if (viewModel.messageAdapter.itemCount > 0) {
                dataBinding.recyclerView.smoothScrollToPosition(viewModel.messageAdapter.itemCount - 1)
            }
        }, 100) // Delaying the scroll by 100 milliseconds
    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }
}