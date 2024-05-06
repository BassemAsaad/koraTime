package com.example.koratime.registration.create_account

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.basic.BasicActivity
import com.example.koratime.R
import com.example.koratime.database.uploadImageToStorage
import com.example.koratime.databinding.ActivityRegisterBinding
import com.example.koratime.registration.log_in.LoginActivity
import com.example.koratime.home.home_user.HomeActivity

class RegisterActivity : BasicActivity<ActivityRegisterBinding, RegisterViewModel>(),RegisterNavigator {
    private lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>
    override fun getLayoutID(): Int {
        return R.layout.activity_register
    }
    override fun initViewModel(): RegisterViewModel {
        return ViewModelProvider(this)[RegisterViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }

    override fun initView() {
        dataBinding.registerVM = viewModel
        viewModel.navigator = this

        setSupportActionBar(dataBinding.toolbar)

        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        dataBinding.logIn.setOnClickListener {
            openLoginActivity()
        }
        dataBinding.radioGroupLayout.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.asPlayer_layout -> {
                    viewModel.showNationalID.value = false
                }
                R.id.asStadiumManager_layout -> {
                    viewModel.showNationalID.value = true
                }
            }
        }

        viewModel.showNationalID.observe(this, Observer { showNationalID ->
            dataBinding.nationalIDLayout.visibility = if (showNationalID) View.VISIBLE else View.GONE
        })


        val defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/kora-time-d21c3.appspot.com/o/images%2Fprofile_picture.png?alt=media&;token=650310ed-94a0-479b-8bb3-ad2f3c4b6489"
        viewModel.imageUrl.value = defaultImageUrl
        openImagePicker()
        dataBinding.profilePictureLayout.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

    }



    @SuppressLint("SetTextI18n")
    fun openImagePicker(){
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

                dataBinding.profilePictureLayout.setImageURI(uri)
                dataBinding.profileTextLayout.text = "Change Picture Chosen"
            } else {
                dataBinding.profileTextLayout.text = "Default Picture"
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                Log.d("PhotoPicker", "No image selected")
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

    override fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}