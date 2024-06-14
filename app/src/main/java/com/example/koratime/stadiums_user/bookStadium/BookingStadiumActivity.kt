package com.example.koratime.stadiums_user.bookStadium

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.TimeSlotsForUserAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.addBookingToFirestore
import com.example.koratime.database.getBookedTimesFromFirestore
import com.example.koratime.database.getMultipleImagesFromFirestore
import com.example.koratime.database.playerDocumentExists
import com.example.koratime.database.removePlayer
import com.example.koratime.databinding.ActivityBookingStadiumBinding
import com.example.koratime.model.StadiumModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
class BookingStadiumActivity : BasicActivity<ActivityBookingStadiumBinding,BookingStadiumViewModel>(),BookingStadiumNavigator {

    private lateinit var stadiumModel : StadiumModel
    private var adapter = TimeSlotsForUserAdapter(emptyList())
    private lateinit var timeSlotsList :List<String>
    private lateinit var availableSlots: List<String>
    private lateinit var bookedTimesList : List<String>
    val slideImageList = mutableListOf<String>()
    private lateinit var selectedDate: String

    override fun getLayoutID(): Int {
        return R.layout.activity_booking_stadium
    }

    override fun initViewModel(): BookingStadiumViewModel {
        return ViewModelProvider(this)[BookingStadiumViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }
    @SuppressLint("SetTextI18n")
    override fun initView() {
        viewModel.navigator=this
        dataBinding.vm = viewModel
//        dataBinding.imageSlider.visibility =View.GONE
//        dataBinding.stadiumImages.visibility =View.GONE

        dataBinding.calendarView.minDate = System.currentTimeMillis()
        dataBinding.calendarView.startAnimation(AnimationUtils.loadAnimation(this, com.google.android.material.R.anim.abc_popup_enter))

        stadiumModel = intent.getParcelableExtra(Constants.STADIUM_USER)!!
        viewModel.stadium = stadiumModel


        setSupportActionBar(dataBinding.toolbar)
        // Enable back button on Toolbar and title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Enable title on Toolbar
        supportActionBar?.title = stadiumModel.stadiumName
        supportActionBar?.setDisplayShowTitleEnabled(true)

        //select date
        selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(Date())

        getAvailableTimes(selectedDate)

        // Add an OnDateChangeListener to the CalendarView
        dataBinding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate = String.format("%02d_%02d_%04d", month + 1, dayOfMonth, year)
            Log.e("Firebase"," Date selected: $selectedDate  ")
            getAvailableTimes(selectedDate)
        }

        // Set up click listener for booking button in the adapter
        adapter.onBookClickListener = object : TimeSlotsForUserAdapter.OnBookClickListener {
            @SuppressLint("SetTextI18n")
            override fun onclick(slot: String, holder: TimeSlotsForUserAdapter.ViewHolder, position: Int) {
                addBookingToFirestore(timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                    stadiumID = stadiumModel.stadiumID!!,
                    date = selectedDate, userId = DataUtils.user!!.id!!,
                    onSuccessListener = {

                        holder.dataBinding.tvTimeSlot.isEnabled=false
                        holder.dataBinding.tvTimeSlot.setTextColor((Color.GRAY))
                        holder.dataBinding.btnBook.isEnabled=false
                        holder.dataBinding.btnBook.text= "Booked"
                        holder.dataBinding.btnBook.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

                        Log.e("Firebase"," ${holder.dataBinding.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} to the stadiumID: ${stadiumModel.stadiumID}")

                        Toast.makeText(this@BookingStadiumActivity,"${holder.dataBinding.tvTimeSlot.text} Booked Successfully", Toast.LENGTH_SHORT).show()

                        //and refresh adapter and recycler view
                        getAvailableTimes(selectedDate)

                         },
                    onFailureListener = {e->
                        Log.e("Firebase"," Error:  ${holder.dataBinding.tvTimeSlot.text}" +
                                " booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} ",e) }
                )

            }
        }



        dataBinding.swipeRefresh.setOnRefreshListener {
            dataBinding.swipeRefresh.isRefreshing = false
            getMultipleImagesFromFirestore(
                stadiumID = stadiumModel.stadiumID!!,
                onSuccessListener = {urls->
                    slideImageList.clear()
                    slideImageList.addAll(urls)
                    Log.e("Firebase"," List of $urls")
                    val imageList = ArrayList<SlideModel>()
                    for ( i in slideImageList ){
                        imageList.add(SlideModel(i, ""))
                    }

                    if (imageList.isNotEmpty()){
                        dataBinding.stadiumImages.visibility = View.VISIBLE
                        dataBinding.imageSlider.visibility = View.VISIBLE
                        dataBinding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
                    }

                },
                onFailureListener = {
                    Log.e("Firebase","Failed To get Images From firestore")
                }
            )
        }

        getMultipleImagesFromFirestore(
            stadiumID = stadiumModel.stadiumID!!,
            onSuccessListener = {urls->
                slideImageList.clear()
                slideImageList.addAll(urls)
                Log.e("Firebase"," List of $urls")
                val imageList = ArrayList<SlideModel>()
                for ( i in slideImageList ){
                    imageList.add(SlideModel(i, ""))
                }

                if (imageList.isNotEmpty()){
                    dataBinding.stadiumImages.visibility = View.VISIBLE
                    dataBinding.imageSlider.visibility = View.VISIBLE
                    dataBinding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
                }

            },
            onFailureListener = {
                Log.e("Firebase","Failed To get Images From firestore")
            }
        )


        playerDocumentExists(
            stadiumID = stadiumModel.stadiumID!!,
            userID = DataUtils.user!!.id!!,
            onSuccessListener = {playerExist->
                if (playerExist){
                    dataBinding.lookForPlayers.text = "Looking For Players..."
                    dataBinding.lookForPlayers.isEnabled = false
                    dataBinding.stopSearching.visibility = View.VISIBLE
                } else{
                    dataBinding.lookForPlayers.text = "Click To Search For Players"
                    dataBinding.lookForPlayers.isEnabled = true
                    dataBinding.stopSearching.visibility = View.GONE
                }
                Log.e("Firebase ","Player Exist ? $playerExist")
            },
            onFailureListener = {e->
                Log.e("Firebase ","Error Finding The player: ",e)

            }
        )
        dataBinding.stopSearching.setOnClickListener {
            removePlayer(
                stadiumID = stadiumModel.stadiumID!!,
                userID = DataUtils.user!!.id!!,
                onSuccessListener = {
                    Log.e("Firebase","Player Removed From Search Successfully")
                    dataBinding.stopSearching.visibility = View.GONE
                    dataBinding.lookForPlayers.text = "Click To Search For Players"
                    dataBinding.lookForPlayers.isEnabled=true
                },
                onFailureListener = {e->
                    Log.e("Firebase","Error Removing Player From Search: ",e)
                }
            )
        }

        dataBinding.stadiumLocation.setOnClickListener {
            showLocation(stadiumModel.latitude!!,stadiumModel.longitude!!)
        }

        dataBinding.stadiumNumber.setOnClickListener {
            intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${stadiumModel.stadiumTelephoneNumber}")
            startActivity(intent)
        }

        dataBinding.bookingPrice.text = "Note: Booking Price is ${stadiumModel.stadiumPrice}EGP (per hour)"
    }


    private fun showLocation(lat: Double, lng: Double) {
        val url = "geo:$lat,$lng?q=$lat,$lng(My Location)"
        val pushIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (pushIntent.resolveActivity(this.packageManager) != null) {
            startActivity(pushIntent)
        }

    }


    private fun getAvailableTimes(date: String) {
        getBookedTimesFromFirestore(
            stadiumID = stadiumModel.stadiumID!!,
            date = date,
            onSuccessListener = { bookedList ->
                bookedTimesList = bookedList
                Log.e("Firebase"," Booked times $bookedTimesList")

                // create opening and closing list
                timeSlotsList = viewModel.createListForOpeningTimes(stadiumModel.opening!!,stadiumModel.closing!!,resources.getStringArray(R.array.time_slots))

                // create list of available times
                availableSlots = viewModel.removeBookedListFromOpeningTimes(timeSlotsList,bookedTimesList)
                Log.e("Available Slots","$availableSlots")

                initializeRecyclerView(availableSlots)
            },
            onFailureListener = { e->
                Log.e("Firebase", "Error fetching booked times", e)
            }
        )
    }

    private fun initializeRecyclerView(availableList : List<String>){
        adapter.updateTimeSlots(availableList)
        dataBinding.recyclerView.adapter=adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }
}