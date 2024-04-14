package com.example.koratime.createRoom

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.rooms.RoomsFragment
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityAddRoomBinding


@Suppress("DEPRECATION")
class AddRoomActivity : BasicActivity< ActivityAddRoomBinding,AddRoomViewModel>(),AddRoomNavigator{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_add_room
    }

    override fun initViewModel(): AddRoomViewModel {
        return ViewModelProvider(this)[AddRoomViewModel::class.java]
    }

    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel

        setSupportActionBar(dataBinding.toolbar)

        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        openImagePicker()


    }

    fun openImagePicker(){
        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        dataBinding.roomImageLayout.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Navigate back to the previous screen when the back button is clicked on the toolbar
        onBackPressed()
        return true
    }

    override fun roomActivity() {
        Toast.makeText(this, "Room Added Successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, RoomsFragment::class.java)
        startActivity(intent)
    }
}