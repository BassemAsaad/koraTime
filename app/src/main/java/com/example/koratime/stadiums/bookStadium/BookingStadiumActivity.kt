package com.example.koratime.stadiums.bookStadium

import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityBookingStadiumBinding
import com.example.koratime.model.StadiumModel


@Suppress("DEPRECATION", "NotifyDataSetChanged")
class BookingStadiumActivity :
    BasicActivity<ActivityBookingStadiumBinding, BookingStadiumViewModel>(),
    BookingStadiumNavigator {

    private lateinit var stadiumModel: StadiumModel

    override val TAG: String
        get() = "BookingStadiumActivity"


    override fun getLayoutID(): Int {
        return R.layout.activity_booking_stadium
    }

    override fun initViewModel(): BookingStadiumViewModel {
        return ViewModelProvider(this)[BookingStadiumViewModel::class.java]
    }

    override fun initView() {
        callback()
    }

    override fun callback() {
        setSupportActionBar(dataBinding.toolbar)
        viewModel.apply {
            dataBinding.vm = viewModel
            navigator = this@BookingStadiumActivity
            stadiumModel = intent.getParcelableExtra(Constants.STADIUM_USER)!!
            stadium = stadiumModel
            timeSlotsArray.value = resources.getStringArray(R.array.time_slots)
            toastMessage.observe(this@BookingStadiumActivity) {
                Toast.makeText(this@BookingStadiumActivity, it, Toast.LENGTH_SHORT).show()
            }
            setUpAdapter()
            adapterCallback()
        }
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = stadiumModel.stadiumName
        }
        dataBinding.apply {
            parentRecyclerView.adapter= viewModel.bookingStadiumAdapter
            swipeRefresh.setOnRefreshListener {
                dataBinding.swipeRefresh.isRefreshing = false
                viewModel.getImagesFromFirestore {
                    viewModel.bookingStadiumAdapter.notifyDataSetChanged()
                }

            }
        }
    }

    override fun showLocation(lat: Double, lng: Double) {
        val url = "geo:$lat,$lng?q=$lat,$lng(My Location)"
        val pushIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (pushIntent.resolveActivity(this.packageManager) != null) {
            startActivity(pushIntent)
        }

    }
    override fun showNumber() {
        intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${stadiumModel.stadiumTelephoneNumber}")
        startActivity(intent)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.booking_stadium_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.stadium_location_menu -> {
                showLocation(stadiumModel.latitude!!, stadiumModel.longitude!!)
                return true

            }
            R.id.call_stadium_menu -> {
                showNumber()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }
}