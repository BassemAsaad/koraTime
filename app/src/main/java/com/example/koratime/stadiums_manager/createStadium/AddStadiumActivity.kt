package com.example.koratime.stadiums_manager.createStadium

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

@Suppress("DEPRECATION")
class AddStadiumActivity : BasicActivity<ActivityAddStadiumBinding,AddStadiumViewModel>(), AddStadiumNavigator {

    private lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_add_stadium
    }

    override fun initViewModel(): AddStadiumViewModel {
        return ViewModelProvider(this)[AddStadiumViewModel::class.java]
    }

    override fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator=this

        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        openImagePicker()
        dataBinding.stadiumImagesLayout.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

    private fun openImagePicker(){
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // photo picker
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                uploadImageToStorage(uri,
                    onSuccessListener = { downloadUri ->
                        Log.e("Firebase Storage:", "Image uploaded successfully")
                        // pass imageUrl to view model
                        viewModel.imageUrl.value = downloadUri.toString()

                    },
                    onFailureListener = {
                        Log.e("Firebase Storage:", it.localizedMessage!!.toString())

                    }
                )

                dataBinding.stadiumImagesLayout.setImageURI(uri)
                dataBinding.stadiumImagesTextLayout.text = "Change Picture Chosen"
            } else {
                dataBinding.stadiumImagesTextLayout.text = "Default Picture"
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                Log.d("PhotoPicker", "No image selected")
            }
        }
    }

    override fun closeActivity() {
        Toast.makeText(this, "Stadium Created Successfully", Toast.LENGTH_SHORT).show()
        finish()
    }


}