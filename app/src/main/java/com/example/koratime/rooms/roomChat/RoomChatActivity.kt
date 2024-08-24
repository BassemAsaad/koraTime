package com.example.koratime.rooms.roomChat

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.koratime.utils.Constants
import com.example.koratime.R
import com.example.koratime.adapters.RoomMessagesAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityRoomChatBinding
import com.example.koratime.model.RoomModel

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
        callback()
    }

    override fun callback() {

        viewModel.apply {
            navigator = this@RoomChatActivity
            roomModel = intent.getParcelableExtra(Constants.ROOM)!!
            room = roomModel
            toastMessage.observe(this@RoomChatActivity) { message ->
                Toast.makeText(this@RoomChatActivity, message, Toast.LENGTH_SHORT).show()
            }
            adapterSetup()
        }
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = roomModel.name
        }
        dataBinding.apply {
            vm = viewModel
            val linearLayoutManager = LinearLayoutManager(this@RoomChatActivity)
            linearLayoutManager.stackFromEnd = true // This will start the layout from the end
            dataBinding.recyclerView.apply {
                layoutManager = linearLayoutManager
                adapter = viewModel.messageAdapter
            }
        }
    }

    override fun scrollToBottom() {
        dataBinding.recyclerView.postDelayed({
            if (messageAdapter.itemCount > 0) {
                dataBinding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
            }
        }, 100) // Delaying the scroll by 100 milliseconds
    }


    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

}