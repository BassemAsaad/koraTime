package com.example.koratime.stadiums.bookStadium

import android.util.Log
import androidx.databinding.ObservableField
import com.example.koratime.DataUtils
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addStadiumRoomToFirestore
import com.example.koratime.database.checkCounterInFirestore
import com.example.koratime.database.getPlayersIdListFromFirestore
import com.example.koratime.database.playerDocumentExists
import com.example.koratime.database.removePlayer
import com.example.koratime.database.resetCounterAndRemovePlayers
import com.example.koratime.database.setPlayerDataAndUpdateCounter
import com.example.koratime.model.StadiumModel


class BookingStadiumViewModel : BasicViewModel<BookingStadiumNavigator>() {
    var stadium: StadiumModel? = null
    val lookForPlayers = ObservableField<String>()
    val buttonEnabled = ObservableField<Boolean>()
    val buttonVisibility = ObservableField<Boolean>()

    companion object {
        private const val TAG = "BookingStadiumViewModel"
    }

    fun createListForOpeningTimes(
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

    fun removeBookedListFromOpeningTimes(
        allTimeSlots: List<String>,
        bookedTimeSlots: List<String>
    ): List<String> {
        return allTimeSlots.filterNot { it in bookedTimeSlots }
    }

    fun lookForPlayers() {
        playerDocumentExists(stadium!!.stadiumID!!, DataUtils.user!!.id!!,
            onSuccessListener = { playerExist ->
                Log.e(TAG, " Player ${DataUtils.user!!.userName} is looking for players")
                if (!playerExist) {
                    setPlayerDataAndUpdateCounter(stadium!!.stadiumID!!, DataUtils.user!!.id!!,
                        onSuccessListener = {
                            Log.e(
                                TAG,
                                "Player added successfully for playersList ${DataUtils.user!!.userName}"
                            )
                            lookForPlayers.set("Looking For Players...")
                            buttonEnabled.set(false)
                            buttonVisibility.set(true)
                            checkCounterInFirestore(stadiumID = stadium!!.stadiumID!!,
                                onSuccessListener = { checkCounter ->
                                    Log.e(TAG, " Counter == 3 ? $checkCounter")
                                    if (checkCounter) {
                                        getPlayersIdListFromFirestore(
                                            stadiumID = stadium!!.stadiumID!!,
                                            onSuccessListener = { playersIDs ->
                                                Log.e(TAG, " PlayersID List $playersIDs")
                                                addStadiumRoomToFirestore(
                                                    stadium!!,
                                                    playersIDs,
                                                    onSuccessListener = {
                                                        Log.e(
                                                            TAG,
                                                            " Stadium Room Created Successfully "
                                                        )

                                                        resetCounterAndRemovePlayers(
                                                            stadiumID = stadium!!.stadiumID!!,
                                                            onSuccessListener = {
                                                                Log.e(
                                                                    TAG,
                                                                    "Document Removed Successfully"
                                                                )

                                                            },
                                                            onFailureListener = {
                                                                Log.e(
                                                                    TAG,
                                                                    "Error Removing Document"
                                                                )
                                                            }
                                                        ) //delete document

                                                    },
                                                    onFailureListener = {
                                                        Log.e(
                                                            TAG,
                                                            " Error Adding Stadium Room ",
                                                            it
                                                        )
                                                    }
                                                )

                                            },
                                            onFailureListener = {
                                                Log.e(TAG, "Error getting PlayersID list: ", it)
                                            }
                                        )// end get list of players id
                                    }

                                },
                                onFailureListener = {
                                    Log.e(
                                        TAG,
                                        "Error getting players count: ",
                                        it
                                    )
                                }
                            )// end check counter

                        },
                        onFailureListener = {
                            Log.e(
                                TAG,
                                "Error adding player and updating counter: ",
                                it
                            )
                        }
                    ) // end set player
                }
            },
            onFailureListener = {
                Log.e(TAG, " Error adding player to search")
            }
        )// end check if player document exist


    }

    fun stopSearching() {
        removePlayer(
            stadiumID = stadium!!.stadiumID!!,
            userID = DataUtils.user!!.id!!,
            onSuccessListener = {
                Log.e(TAG, "Player Removed From Search Successfully")
                buttonVisibility.set(false)
                lookForPlayers.set("Click To Search For Players")
                buttonEnabled.set(true)
            },
            onFailureListener = { e ->
                Log.e(TAG, "Error Removing Player From Search: ", e)
            }
        )
    }
}

