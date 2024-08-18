package com.example.koratime.stadiums.manageStadium

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityManageStadiumBinding
import com.example.koratime.model.StadiumModel
import com.example.koratime.stadiums.bookingRequests.BookingRequestsActivity

@Suppress( "SetTextI18n", "DEPRECATION")
class ManageStadiumActivity : BasicActivity<ActivityManageStadiumBinding, ManageStadiumViewModel>(),
    ManageStadiumNavigator {
    override val TAG: String
        get() = "ManageStadiumActivity"
    private lateinit var stadiumModel: StadiumModel

    private val getMultipleContents = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
        // photo picker
        if (uris.isNotEmpty()) {
            viewModel.showLoading.value = true
            log("PhotoPicker: Selected URIs: $uris")
            //upload images to storage
            viewModel.uploadImagesToStorage(uris)

        } else {
            viewModel.manageStadiumAdapter.changeImagePickerText("No image selected")
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            log("PhotoPicker: No media selected")
        }


    }


    override fun getLayoutID(): Int {
        return R.layout.activity_manage_stadium
    }

    override fun initViewModel(): ManageStadiumViewModel {
        return ViewModelProvider(this)[ManageStadiumViewModel::class.java]
    }

    override fun initView() {
        callback()
    }

    override fun callback() {
        setSupportActionBar(dataBinding.toolbar)
        viewModel.apply {
            dataBinding.vm = viewModel
            navigator = this@ManageStadiumActivity
            stadiumModel = intent.getParcelableExtra(Constants.STADIUM_MANAGER)!!
            stadium = stadiumModel
            timeSlotsArray.value = resources.getStringArray(R.array.time_slots)
            toastMessage.observe(this@ManageStadiumActivity) {
                Toast.makeText(this@ManageStadiumActivity, it, Toast.LENGTH_SHORT).show()
            }
            setUpAdapter()
            adapterCallback()
        }
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = stadiumModel.stadiumName
        }
        dataBinding.apply {
            parentRecyclerView.adapter= viewModel.manageStadiumAdapter
            swipeRefresh.setOnRefreshListener {
                dataBinding.swipeRefresh.isRefreshing = false
                viewModel.getImagesFromFirestore {
                    viewModel.manageStadiumAdapter.changeImageSlider(it)
                }

            }


        }
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stadium_manager_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.booking_requests -> {
                openBookingRequestsActivity()
                return true
            }
            R.id.delete_stadium -> {
                viewModel.deleteStadium()
                return true
            }
        }

        return  super.onOptionsItemSelected(item)
    }
    override fun closeActivity() {
        finish()
    }
    override fun openBookingRequestsActivity() {
        val intent = Intent(this, BookingRequestsActivity::class.java)
        intent.putExtra(Constants.STADIUM_BOOKING_REQUESTS, viewModel.stadium)
        startActivity(intent)

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun openImagePicker() {
        getMultipleContents.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}

