package com.example.koratime.createRoom

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.rooms.RoomsActivity
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityAddRoomBinding

class AddRoomActivity : BasicActivity< ActivityAddRoomBinding,AddRoomViewModel>(),AddRoomNavigator{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_room)
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_add_room
    }

    override fun initViewModel(): AddRoomViewModel {
        return ViewModelProvider(this).get(AddRoomViewModel::class.java)
    }

    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel


    }
    override fun roomActivity() {
        Toast.makeText(this, "Room Added Successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, RoomsActivity::class.java)
        startActivity(intent)
    }
}