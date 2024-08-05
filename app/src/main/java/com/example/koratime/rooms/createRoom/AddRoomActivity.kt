package com.example.koratime.rooms.createRoom


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
import com.example.koratime.databinding.ActivityAddRoomBinding

@Suppress("DEPRECATION","SetTextI18n")
class AddRoomActivity : BasicActivity< ActivityAddRoomBinding, AddRoomViewModel>(), AddRoomNavigator {

    private lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>

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
        setSupportActionBar(dataBinding.toolbar)

        viewModel.apply {
            navigator = this@AddRoomActivity
            // If no image is selected, use the default image URL
            imageUrl.value = getString(R.string.default_room_picture)
            toastMessage.observe( this@AddRoomActivity) { message ->
                Toast.makeText(this@AddRoomActivity, message, Toast.LENGTH_SHORT).show()
            }
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        dataBinding.apply {
            vm = viewModel
            openImagePicker()
            roomImageLayout.setOnClickListener {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    private fun openImagePicker(){
        // Registers a photo picker activity launcher in single-select mode.
         pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // photo picker
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                viewModel.showLoading.value = true
                uploadImageToStorage(uri,
                    onSuccessListener = { downloadUri ->
                        Log.e("Firebase Storage:", "Image uploaded successfully")
                        // pass imageUrl to view model
                        viewModel.apply {
                            imageUrl.value = downloadUri.toString()
                            showLoading.value = false
                        }

                    },
                    onFailureListener = {
                        Log.e("Firebase Storage:", it.localizedMessage!!.toString())
                        viewModel.showLoading.value = false

                    }
                )

                dataBinding.apply {
                    roomImageLayout.setImageURI(uri)
                    roomImageTextLayout.text = "Change Picture Chosen"
                }
            } else {
                dataBinding.roomImageTextLayout.text = "Default Picture"
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                Log.d("PhotoPicker", "No image selected")
                viewModel.showLoading.value = false

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

    override fun roomsFragment() {
        Toast.makeText(this, "Room Added Successfully", Toast.LENGTH_SHORT).show()
        finish()

    }

}
