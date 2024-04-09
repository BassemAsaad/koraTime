package com.example.koratime.rooms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.createRoom.AddRoomActivity
import com.example.koratime.databinding.ActivityRoomsBinding
import com.example.koratime.home.HomeActivity

class RoomsActivity : BasicActivity<ActivityRoomsBinding,RoomsViewModel>(),RoomsNavigator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutID(): Int {
        return R.layout.activity_rooms
    }

    override fun initViewModel(): RoomsViewModel {
        return ViewModelProvider(this).get(RoomsViewModel::class.java)
    }

    override fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator=this
    }

    override fun addRoomActivity() {
        val intent = Intent(this, AddRoomActivity::class.java)
        startActivity(intent)
    }


}