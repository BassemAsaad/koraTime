package com.example.koratime.chat.chatFriends

import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.FriendMessagesAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.getFriendMessagesFromFirestore
import com.example.koratime.databinding.ActivityChatFriendsBinding
import com.example.koratime.model.FriendMessageModel
import com.example.koratime.model.FriendModel
import com.google.firebase.firestore.DocumentChange

@Suppress("DEPRECATION")
class ChatFriendsActivity : BasicActivity<ActivityChatFriendsBinding, ChatFriendsViewModel>(),
    ChatFriendsNavigator {
    override val TAG: String
        get() = "ChatFriendsActivity"
    private lateinit var friendModel: FriendModel
    private val messageAdapter = FriendMessagesAdapter()
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
            toastMessage.observe(this@ChatFriendsActivity, Observer { message ->
                Toast.makeText(this@ChatFriendsActivity, message, Toast.LENGTH_SHORT).show()
            })
        }
        dataBinding.apply {
            vm = viewModel
            val linearLayoutManager = LinearLayoutManager(this@ChatFriendsActivity)
            linearLayoutManager.stackFromEnd = true // This will start the layout from the end
            recyclerView.apply {
                layoutManager = linearLayoutManager
                adapter = messageAdapter
            }
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }


    private fun scrollToBottom() {
        dataBinding.recyclerView.postDelayed({
            if (messageAdapter.itemCount > 0) {
                dataBinding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
            }
        }, 100) // Delaying the scroll by 100 milliseconds
    }

    override fun onStart() {
        super.onStart()
        listenForMessageUpdate()
    }

    private fun listenForMessageUpdate() {
        getFriendMessagesFromFirestore(
            senderID = DataUtils.user!!.id!!,
            friendshipID = friendModel.friendshipID!!,
        ).addSnapshotListener { snapshots, error ->
            if (error != null) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            } else {
                val newMessageList = mutableListOf<FriendMessageModel?>()
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val message = dc.document.toObject(FriendMessageModel::class.java)
                        newMessageList.add(message)
                    }
                }
                messageAdapter.changeData(newMessageList)
                scrollToBottom() // Scroll to the last position

            }
        }
    }


}