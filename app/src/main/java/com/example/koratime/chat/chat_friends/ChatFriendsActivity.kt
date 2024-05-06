package com.example.koratime.chat.chat_friends

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
class ChatFriendsActivity : BasicActivity<ActivityChatFriendsBinding,ChatFriendsViewModel>(),ChatFriendsNavigator {
    private lateinit var friendModel : FriendModel
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
        listenForMessageUpdate()
        dataBinding.recyclerView.adapter=messageAdapter

        viewModel.toastMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })

    }
    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

    private fun listenForMessageUpdate (){
        getFriendMessagesFromFirestore(
            senderID =DataUtils.user!!.id!!,
            friendshipID = friendModel.friendshipID!!,
        ).addSnapshotListener { snapshots , error ->
            if (error!=null){
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }else{
                val newMessageList = mutableListOf<FriendMessageModel?>()
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val message = dc.document.toObject(FriendMessageModel::class.java)
                            newMessageList.add(message)
                        }
                        DocumentChange.Type.MODIFIED -> Log.e("Firebase", "Error")
                        DocumentChange.Type.REMOVED -> Log.e("Firebase", "Error")
                    }
                }
                messageAdapter.changeData(newMessageList)
            }
        }
    }


}