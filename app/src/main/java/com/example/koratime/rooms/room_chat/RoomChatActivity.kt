package com.example.koratime.rooms.room_chat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
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
class RoomChatActivity : BasicActivity<ActivityRoomChatBinding,RoomChatViewModel>(),RoomChatNavigator{
    lateinit var room : RoomModel
    private val messageAdapter = RoomMessagesAdapter()
    override fun getLayoutID(): Int {
        return R.layout.activity_room_chat
    }

    override fun initViewModel(): RoomChatViewModel {
        return ViewModelProvider(this)[RoomChatViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }

    override fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator = this

        room = intent.getParcelableExtra(Constants.ROOM)!!
        viewModel.room = room

        setSupportActionBar(dataBinding.toolbar)
        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Enable title on Toolbar
        supportActionBar?.title = room.name
        supportActionBar?.setDisplayShowTitleEnabled(true)
        listenForMessageUpdate()
        dataBinding.recyclerView.adapter=messageAdapter



    }
    private fun listenForMessageUpdate (){
        getRoomMessagesFromFirestore(room.roomID!!)
            .addSnapshotListener { snapshots , error ->
                if (error!=null){
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }else{
                    val newMessageList = mutableListOf<RoomMessageModel?>()
                    for (dc in snapshots!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val message = dc.document.toObject(RoomMessageModel::class.java)
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
    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

}