package com.example.koratime.rooms.room_chat

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.adapters.RoomMessagesAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.getRoomMessagesFromFirestore
import com.example.koratime.databinding.ActivityRoomChatBinding
import com.example.koratime.model.RoomMessageModel
import com.example.koratime.model.RoomModel
import com.google.firebase.firestore.DocumentChange

@Suppress("DEPRECATION")
class RoomChatActivity : BasicActivity<ActivityRoomChatBinding, RoomChatViewModel>(),
    RoomChatNavigator {
    override val TAG: String
        get() = "RoomChatActivity"
    private lateinit var roomModel: RoomModel
    private val messageAdapter = RoomMessagesAdapter()
    override fun getLayoutID(): Int {
        return R.layout.activity_room_chat
    }

    override fun initViewModel(): RoomChatViewModel {
        return ViewModelProvider(this)[RoomChatViewModel::class.java]
    }


    override fun initView() {
        setSupportActionBar(dataBinding.toolbar)
        roomModel = intent.getParcelableExtra(Constants.ROOM)!!
        callback()
    }

    override fun callback() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = roomModel.name
        }
        viewModel.apply {
            navigator = this@RoomChatActivity
            room = roomModel
            toastMessage.observe(this@RoomChatActivity) { message ->
                Toast.makeText(this@RoomChatActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
        dataBinding.apply {
            vm = viewModel
            val linearLayoutManager = LinearLayoutManager(this@RoomChatActivity)
            linearLayoutManager.stackFromEnd = true // This will start the layout from the end
            dataBinding.recyclerView.apply {
                layoutManager = linearLayoutManager
                adapter = messageAdapter
            }
        }
    }

    override fun onStart() {
        super.onStart()
        listenForMessageUpdate()
    }
    private fun scrollToBottom() {
        dataBinding.recyclerView.postDelayed({
            if (messageAdapter.itemCount > 0) {
                dataBinding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
            }
        }, 100) // Delaying the scroll by 100 milliseconds
    }

    private fun listenForMessageUpdate() {
        getRoomMessagesFromFirestore(roomModel.roomID!!)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
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
                    scrollToBottom()
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

}