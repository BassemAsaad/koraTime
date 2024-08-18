package com.example.koratime.stadiums.bookStadium

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.denzcoskun.imageslider.models.SlideModel
import com.example.koratime.DataUtils
import com.example.koratime.adapters.CalendarAdapter
import com.example.koratime.adapters.TimeSlotsForUserAdapter
import com.example.koratime.adapters.parentAdapters.BookingStadiumAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addBookingToFirestore
import com.example.koratime.database.addStadiumRoomToFirestore
import com.example.koratime.database.checkCounterInFirestore
import com.example.koratime.database.getBookedTimesFromFirestore
import com.example.koratime.database.getMultipleImagesFromFirestore
import com.example.koratime.database.getPlayersIdListFromFirestore
import com.example.koratime.database.playerDocumentExists
import com.example.koratime.database.removePlayer
import com.example.koratime.database.resetCounterAndRemovePlayers
import com.example.koratime.database.setPlayerDataAndUpdateCounter
import com.example.koratime.model.StadiumModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Suppress("SetTextI18n")
class BookingStadiumViewModel : BasicViewModel<BookingStadiumNavigator>() {
    override val TAG: String
        get() = BookingStadiumViewModel::class.java.simpleName

    var stadium: StadiumModel? = null
    val lookForPlayers = ObservableField<String>()
    val buttonEnabled = ObservableField<Boolean>()
    val buttonVisibility = ObservableField<Boolean>()

    var timeSlotsArray = MutableLiveData<Array<String>>()
    val toastMessage = MutableLiveData<String>()

    private var selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(Date())
    private var selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

    private var calendarAdapter = CalendarAdapter(emptyList())
    private var timeSlotsAdapter = TimeSlotsForUserAdapter(emptyList())
    lateinit var bookingStadiumAdapter: BookingStadiumAdapter
    private lateinit var timeSlotsList: List<String>
    private lateinit var availableSlots: MutableList<String>
    private lateinit var bookedTimesList: List<String>

    fun setUpAdapter() {
        getImagesFromFirestore {
            bookingStadiumAdapter.changeImageSlider(it)
        }
        getTimeSlots(selectedDate)
        calendarAdapter = CalendarAdapter(generateNextTwoWeeks())
        bookingStadiumAdapter =
            BookingStadiumAdapter(calendarAdapter, timeSlotsAdapter, stadium!!.stadiumID!!)

    }

    fun adapterCallback() {
        timeSlotsAdapter.onBookClickListener =
            object : TimeSlotsForUserAdapter.OnBookClickListener {
                @SuppressLint("SetTextI18n")
                override fun onclick(
                    slot: String,
                    holder: TimeSlotsForUserAdapter.ViewHolder,
                    position: Int
                ) {
                    addBookingToFirestore(
                        timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                        stadiumID = stadium!!.stadiumID!!,
                        date = selectedDate,
                        user = DataUtils.user!!,
                        onSuccessListener = {
                            holder.dataBinding.apply {
                                tvTimeSlot.isEnabled = false
                                tvTimeSlot.setTextColor((Color.GRAY))
                                btnBook.isEnabled = false
                                btnBook.text = "Booked"
                                btnBook.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                            }
                            log(
                                " ${holder.dataBinding.tvTimeSlot.text} booked on  $selectedDate" +
                                        " from userId: ${DataUtils.user!!.userName}" +
                                        " to the stadiumID: ${stadium!!.stadiumName}"
                            )

                            toastMessage.value =
                                "${holder.dataBinding.tvTimeSlot.text} Booked Successfully"


                        },
                        onFailureListener = { e ->
                            log("Error Removing Book from firestore $e")
                        }
                    )

                }
            }
        bookingStadiumAdapter.onSearchClickListener =
            object : BookingStadiumAdapter.OnSearchClickListener {
                override fun onSearchClick(
                    holder: BookingStadiumAdapter.PlayersSearchViewHolder,
                    stadiumId: String
                ) {
                    playerDocumentExists(stadiumId, DataUtils.user!!.id!!,
                        onSuccessListener = { playerExist ->
                            log(" Player ${DataUtils.user!!.userName} is looking for players")
                            if (!playerExist) {
                                setPlayerDataAndUpdateCounter(stadiumId, DataUtils.user!!.id!!,
                                    onSuccessListener = {
                                        log("Player added successfully for playersList ${DataUtils.user!!.userName}")
                                        holder.dataBinding.apply {
                                            stopSearching.visibility = View.VISIBLE
                                            lookForPlayers.text = "Looking For Players..."
                                            lookForPlayers.isEnabled = false
                                        }
                                        checkCounterInFirestore(
                                            stadiumID = stadiumId,
                                            onSuccessListener = { checkCounter ->
                                                log(" Counter == 3 ? $checkCounter")
                                                if (checkCounter) {
                                                    getPlayersIdListFromFirestore(
                                                        stadiumID = stadiumId,
                                                        onSuccessListener = { playersIDs ->
                                                            log("PlayersID List $playersIDs")
                                                            addStadiumRoomToFirestore(
                                                                stadium!!,
                                                                playersIDs,
                                                                onSuccessListener = {
                                                                    log("Stadium Room Created Successfully")
                                                                    resetCounterAndRemovePlayers(
                                                                        stadiumID = stadiumId,
                                                                        onSuccessListener = {
                                                                            log("Document Removed Successfully")
                                                                        },
                                                                        onFailureListener = {
                                                                            log("Error Removing Document: $it")
                                                                        }
                                                                    ) //delete document

                                                                },
                                                                onFailureListener = {
                                                                    log("Error Adding Stadium Room $it ")
                                                                }
                                                            )

                                                        },
                                                        onFailureListener = {
                                                            log("Error getting PlayersID list: $it")
                                                        }
                                                    )// end get list of players id
                                                }

                                            },
                                            onFailureListener = {
                                                log("Error getting players count: $it")
                                            }
                                        )// end check counter

                                    },
                                    onFailureListener = {
                                        log("Error adding player and updating counter: $it")
                                    }
                                ) // end set player
                            }
                        },
                        onFailureListener = {
                            log(" Error adding player to search: $it")
                        }
                    )// end check if player document exist
                }

                override fun onStopSearchClick(
                    holder: BookingStadiumAdapter.PlayersSearchViewHolder,
                    stadiumId: String
                ) {
                    removePlayer(
                        stadiumID = stadiumId,
                        userID = DataUtils.user!!.id!!,
                        onSuccessListener = {
                            log("Player Removed From Search Successfully")
                            holder.dataBinding.apply {
                                stopSearching.visibility = View.GONE
                                lookForPlayers.text = "Click To Search For Players"
                                lookForPlayers.isEnabled = true
                            }
                        },
                        onFailureListener = { e ->
                            log("Error Removing Player From Search: $e ")
                        }
                    )
                }
            }
        calendarAdapter.onItemClickListener = object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(
                date: Date?,
                holder: CalendarAdapter.CalendarViewHolder,
                position: Int
            ) {
                selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(date!!)
                log("Date selected: $selectedDate")
                selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)

                calendarAdapter.changeDate(date)
                bookingStadiumAdapter.changeDateTitle(selectedYear)
                getTimeSlots(selectedDate)
            }
        }

    }


    private fun getTimeSlots(date: String) {
        getBookedTimesFromFirestore(
            stadiumID = stadium!!.stadiumID!!,
            date = date,
            onSuccessListener = { bookedList ->

                bookedTimesList = bookedList
                Log.e(TAG, " Booked times $bookedTimesList")

                // create opening and closing list
                timeSlotsList = createListForOpeningTimes(
                    stadium!!.opening!!,
                    stadium!!.closing!!,
                    timeSlotsArray.value!!
                )

                // create list of available times
                availableSlots =
                    removeBookedListFromOpeningTimes(timeSlotsList, bookedTimesList)
                        .toMutableList()
                Log.e(TAG, "Available Slots : $availableSlots")

                timeSlotsAdapter.updateTimeSlots(availableSlots)
            },
            onFailureListener = { e ->
                Log.e(TAG, "Error fetching booked times", e)
            }
        )
    }

    private fun createListForOpeningTimes(
        openingIndex: Int,
        closingIndex: Int,
        timeSlotsArray: Array<String>
    ): List<String> {
        val availableTimeSlots = mutableListOf<String>()

        // Ensure closing index is greater than opening index and within bounds
        if ((openingIndex >= 0) && (closingIndex >= openingIndex) && (closingIndex < timeSlotsArray.size)) {
            for (i in openingIndex..closingIndex) {
                availableTimeSlots.add(timeSlotsArray[i])
            }
        }

        return availableTimeSlots
    }

    private fun removeBookedListFromOpeningTimes(
        allTimeSlots: List<String>,
        bookedTimeSlots: List<String>
    ): List<String> {
        return allTimeSlots.filterNot { it in bookedTimeSlots }
    }

    private fun generateNextTwoWeeks(): List<Date> {
        // Initialize calendar to current date
        val calendar = Calendar.getInstance()
        val daysList = mutableListOf<Date>()

        // Loop to generate next 14 days including today
        for (i in 0..13) {
            // Add current date to the list
            daysList.add(calendar.time)
            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return daysList
    }

    fun getImagesFromFirestore(callback: (ArrayList<SlideModel>) -> Unit) {
        getMultipleImagesFromFirestore(
            stadiumID = stadium!!.stadiumID!!,
            onSuccessListener = { urls ->
                val imageList = ArrayList<SlideModel>()
                log("List of image urls: $urls")
                urls.forEach { imageList.add(SlideModel(it, "")) }
                callback(imageList) // Invoke the callback with the result
            },
            onFailureListener = {
                log("Failed To get ImagesFrom firestore $it")
                callback(arrayListOf()) // Return an empty list or handle the error
            }
        )
    }


}

