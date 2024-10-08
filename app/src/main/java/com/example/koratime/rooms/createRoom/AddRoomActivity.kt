package com.example.koratime.rooms.createRoom


import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityAddRoomBinding

@Suppress("DEPRECATION", "SetTextI18n")
class AddRoomActivity : BasicActivity<ActivityAddRoomBinding, AddRoomViewModel>(),
    AddRoomNavigator {
    override val TAG: String
        get() = "AddRoomActivity"
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // photo picker
            if (uri != null) {
                log("PhotoPicker selected URI: $uri")
                viewModel.imagesUri.value = uri
                dataBinding.apply {
                    roomImageLayout.setImageURI(uri)
                    roomImageTextLayout.text = "Change Picture Chosen"
                }
            } else {
                dataBinding.roomImageTextLayout.text = "Default Picture"
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                log("PhotoPicker: No media selected")
                viewModel.showLoading.value = false

            }
        }

    override fun getLayoutID(): Int {
        return R.layout.activity_add_room
    }

    override fun initViewModel(): AddRoomViewModel {
        return ViewModelProvider(this)[AddRoomViewModel::class.java]
    }


    override fun initView() {
        setSupportActionBar(dataBinding.toolbar)
        callback()
    }

    override fun callback() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        viewModel.apply {
            navigator = this@AddRoomActivity
            // If no image is selected, use the default image URL
            imageUrl.value = getString(R.string.default_room_picture)
            toastMessage.observe(this@AddRoomActivity) { message ->
                Toast.makeText(this@AddRoomActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
        dataBinding.apply {
            vm = viewModel

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

    override fun openImagePicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun closeActivity() {
        Toast.makeText(this, "Room Added Successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

}
