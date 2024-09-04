package com.example.koratime.stadiumUser.bookStadium

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.denzcoskun.imageslider.models.SlideModel
import com.example.koratime.adapters.CalendarAdapter
import com.example.koratime.adapters.TimeSlotsAdapter
import com.example.koratime.adapters.parentAdapters.BookingStadiumAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.BookingModel
import com.example.koratime.model.StadiumModel
import com.example.koratime.utils.DataUtils
import com.example.koratime.utils.addBookingToFirestore
import com.example.koratime.utils.addStadiumRoomToFirestore
import com.example.koratime.utils.checkCounterInFirestore
import com.example.koratime.utils.checkUserSameSlotBooking
import com.example.koratime.utils.getBookedTimesFromFirestore
import com.example.koratime.utils.getMultipleImagesFromFirestore
import com.example.koratime.utils.getPlayersIdListFromFirestore
import com.example.koratime.utils.playerDocumentExists
import com.example.koratime.utils.removePlayer
import com.example.koratime.utils.resetCounterAndRemovePlayers
import com.example.koratime.utils.setPlayerDataAndUpdateCounter
import com.google.firebase.firestore.DocumentChange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Suppress("SetTextI18n")
class BookingStadiumViewModel : BasicViewModel<BookingStadiumNavigator>() {
    override val TAG: String
        get() = BookingStadiumViewModel::class.java.simpleName

    var stadium: StadiumModel? = null
    var timeSlotsArray = MutableLiveData<Array<String>>()
    val toastMessage = MutableLiveData<String>()

    private var selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(Date())
    private var selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())


    private var timeSlotsList = mutableListOf<String>()
    private var availableSlots = mutableListOf<String>()
    private var bookedTimesList = mutableListOf<String>()

    private var calendarAdapter = CalendarAdapter(emptyList())
    private var timeSlotsAdapter = TimeSlotsAdapter(timeSlotsList,bookedTimesList)
    lateinit var bookingStadiumAdapter: BookingStadiumAdapter
    fun setUpAdapter() {
        calendarAdapter = CalendarAdapter(generateNextTwoWeeks())

        timeSlotsList = createListForOpeningTimes(
            stadium!!.opening!!,
            stadium!!.closing!!,
            timeSlotsArray.value!!
        )
        log("TimeSlots List: $timeSlotsList")

        getTimeSlots(selectedDate)
        bookingStadiumAdapter =
            BookingStadiumAdapter(calendarAdapter, timeSlotsAdapter, stadium!!.stadiumID!!)
        getImagesFromFirestore {
            bookingStadiumAdapter.changeImageSlider(it)
        }
    }

    fun adapterCallback() {

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
        calendarAdapter.onItemClickListener =
            object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(
                date: Date?,
                holder: CalendarAdapter.CalendarViewHolder,
                position: Int
            ) {
                bookedTimesList.clear()
                availableSlots.clear()

                selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(date!!)
                log("Date selected: $selectedDate")
                selectedYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)

                calendarAdapter.changeDate(date)
                bookingStadiumAdapter.changeDateTitle(selectedYear)
                getTimeSlots(selectedDate)
            }
        }
        timeSlotsAdapter.onBookClickListener =
            object : TimeSlotsAdapter.OnBookClickListener {
                override fun onclick(
                    slot: String,
                    holder: TimeSlotsAdapter.ViewHolder,
                    position: Int
                ) {
                    checkUserSameSlotBooking(
                        user = DataUtils.user!!,
                        date = selectedDate,
                        timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                        onSuccessListener = { check->
                            if (check){
                                toastMessage.value = "You already have a booking for ${holder.dataBinding.tvTimeSlot.text}."

                            } else{
                                addBookingToFirestore(
                                    timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                                    stadium = stadium!!,
                                    date = selectedDate,
                                    user = DataUtils.user!!,
                                    onSuccessListener = {
                                        toastMessage.value = "${holder.dataBinding.tvTimeSlot.text} Booked Successfully."
                                        log("${holder.dataBinding.tvTimeSlot.text} Booked Successfully.")
                                    },
                                    onFailureListener = { e ->
                                        log("Error Adding Booking to firestore $e")
                                    }
                                )
                            }

                        },
                        onFailureListener = {
                            log("Error Checking User Same Slot Booking $it")
                        }

                    )

                }

            }

    }


    private fun getTimeSlots(date: String) {
        getBookedTimesFromFirestore(stadiumID = stadium!!.stadiumID!!, date = date)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    log("Error getting booked times from firestore $error")
                    toastMessage.value = "Error getting booked times from firestore $error"
                }else{
                    for (dc in value!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val booking = dc.document.toObject(BookingModel::class.java)
                                bookedTimesList.add(booking.timeSlot!!)
                                log("Book added ${booking.timeSlot}")
                            }
                            DocumentChange.Type.REMOVED -> {
                                val booking = dc.document.toObject(BookingModel::class.java)
                                bookedTimesList.remove(booking.timeSlot!!)
                                log("Book removed ${booking.timeSlot}")
                            }
                            DocumentChange.Type.MODIFIED -> { log("Modified") }
                        }
                    }
                    log("BookedTimesList: $bookedTimesList")
                    availableSlots = removeBookedListFromOpeningTimes(timeSlotsList, bookedTimesList)
                    log("AvailableSlots List: $availableSlots")

                    timeSlotsAdapter.updateTimeSlots(timeSlotsList, bookedTimesList)
                }
            }
        }


    private fun createListForOpeningTimes(
        openingIndex: Int,
        closingIndex: Int,
        timeSlotsArray: Array<String>
    ): MutableList<String> {
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
        allTimeSlots: MutableList<String>,
        bookedTimeSlots: MutableList<String>
    ): MutableList<String> {
        return allTimeSlots.filterNot { it in bookedTimeSlots }.toMutableList()
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

