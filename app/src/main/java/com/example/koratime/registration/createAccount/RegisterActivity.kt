package com.example.koratime.registration.createAccount

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.uploadImageToStorage
import com.example.koratime.databinding.ActivityRegisterBinding
import com.example.koratime.registration.login.LoginActivity

@Suppress("DEPRECATION","SetTextI18n")
class RegisterActivity : BasicActivity<ActivityRegisterBinding, RegisterViewModel>(),
    RegisterNavigator {
    override val TAG: String
        get() = "RegisterActivity"
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // photo picker
        if (uri != null) {
            log("PhotoPicker selected URI: $uri")
            viewModel.imagesUri.value = uri
            dataBinding.apply {
                profilePictureLayout.setImageURI(uri)
                profileTextLayout.text = "Change Picture"
            }
        } else {
            dataBinding.profileTextLayout.text = "Default Picture"
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            log("PhotoPicker: No media selected")
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_register
    }

    override fun initViewModel(): RegisterViewModel {
        return ViewModelProvider(this)[RegisterViewModel::class.java]
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
            navigator = this@RegisterActivity
            dataBinding.registerVM = viewModel
            imageUrl.value = getString(R.string.default_profile_picture)

            showNationalID.observe(this@RegisterActivity) { showNationalID ->
                dataBinding.nationalIDLayout.visibility =
                    if (showNationalID) View.VISIBLE else View.GONE
            }
            toastMessage.observe(this@RegisterActivity) { message ->
                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
        dataBinding.apply {
            radioGroupLayout.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.asPlayer_layout -> {
                        viewModel.showNationalID.value = false
                    }

                    R.id.asStadiumManager_layout -> {
                        viewModel.showNationalID.value = true
                    }
                }
            }
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
    override fun openLoginActivity() {
        closeActivity()
    }
    override fun closeActivity() {
        finish()
    }

}