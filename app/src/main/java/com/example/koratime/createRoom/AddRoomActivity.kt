package com.example.koratime.createRoom

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.rooms.RoomsFragment
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityAddRoomBinding

@Suppress("DEPRECATION")
class AddRoomActivity : BasicActivity< ActivityAddRoomBinding,AddRoomViewModel>(),AddRoomNavigator{

    lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>

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

    @SuppressLint("SetTextI18n")
    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel

        setSupportActionBar(dataBinding.toolbar)

        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        openImagePicker()
        dataBinding.roomImageLayout.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

    }
    fun openImagePicker(){
        // Registers a photo picker activity launcher in single-select mode.
         pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                dataBinding.roomImageLayout.setImageURI(uri)
                dataBinding.roomImageTextLayout.text = "Change Picture Chosen"
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        // go back to the previous fragment when back button clicked on the toolbar
        onBackPressed()
        return true
    }

    override fun roomActivity() {
        Toast.makeText(this, "Room Added Successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, RoomsFragment::class.java)
        startActivity(intent)
    }
}