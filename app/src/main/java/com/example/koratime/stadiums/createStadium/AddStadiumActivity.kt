package com.example.koratime.stadiums.createStadium

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.uploadImageToStorage
import com.example.koratime.databinding.ActivityAddStadiumBinding
import com.example.koratime.rooms.RoomsFragment
import com.example.koratime.stadiums.StadiumsFragment


class AddStadiumActivity : BasicActivity<ActivityAddStadiumBinding, AddStadiumViewModel>(), AddStadiumNavigator {

    lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_stadium)
        initView()
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_add_stadium
    }

    override fun initViewModel(): AddStadiumViewModel {
        return ViewModelProvider(this)[AddStadiumViewModel::class.java]
    }

    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel



    }



    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }


    override fun stadiumFragment() {
        Toast.makeText(this, "Stadium Added Successfully", Toast.LENGTH_SHORT).show()
        finish()
    }







}