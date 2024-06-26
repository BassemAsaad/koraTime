package com.example.koratime.stadiums_user.bookStadium

import android.util.Log
import androidx.databinding.ObservableField
import com.example.koratime.DataUtils
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.addStadiumRoomToFirestore
import com.example.koratime.database.checkCounterInFirestore
import com.example.koratime.database.getPlayersIdListFromFirestore
import com.example.koratime.database.playerDocumentExists
import com.example.koratime.database.resetCounterAndRemovePlayers
import com.example.koratime.database.setPlayerDataAndUpdateCounter
import com.example.koratime.model.StadiumModel


class BookingStadiumViewModel : BasicViewModel<BookingStadiumNavigator>() {
    var stadium : StadiumModel?=null
    val lookForPlayers = ObservableField<String>()
    val buttonEnabled = ObservableField<Boolean>()
    val buttonVisibility= ObservableField<Boolean>()
    fun createListForOpeningTimes(openingIndex: Int, closingIndex: Int, timeSlotsArray: Array<String>): List<String> {
        val availableTimeSlots = mutableListOf<String>()

        // Ensure closing index is greater than opening index and within bounds
        if ((openingIndex >= 0) && (closingIndex >= openingIndex) && (closingIndex < timeSlotsArray.size)) {
            for (i in openingIndex..closingIndex) {
                availableTimeSlots.add(timeSlotsArray[i])
            }
        }

        return availableTimeSlots
    }
    fun removeBookedListFromOpeningTimes(allTimeSlots: List<String>, bookedTimeSlots: List<String>): List<String> {
        return allTimeSlots.filterNot { it in bookedTimeSlots }
    }

//onSuccessListenerBoolean = {
//                Log.e("Firebase "," Player Document Exist $it")
//            },
    fun LookForPlayers(){

    playerDocumentExists(stadium!!.stadiumID!!, DataUtils.user!!.id!!,
        onSuccessListener = {playerExist->
            Log.e("Firebase"," Player ${DataUtils.user!!.userName} is looking for players")
            if (!playerExist){
                setPlayerDataAndUpdateCounter(stadium!!.stadiumID!!, DataUtils.user!!.id!!,
                    onSuccessListener = {
                        Log.e("Firebase ","Document added successfully for player ${DataUtils.user!!.id}")
                        lookForPlayers.set("Looking For Players...")
                        buttonEnabled.set(false)
                        buttonVisibility.set(true)
                        checkCounterInFirestore(stadiumID = stadium!!.stadiumID!!,
                            onSuccessListener = {playersCounter->
                                Log.e("Firebase"," Counter == 3 ? $playersCounter")
                                if (playersCounter){
                                    getPlayersIdListFromFirestore(
                                        stadiumID = stadium!!.stadiumID!!,
                                        onSuccessListener = {playersIDs->
                                            Log.e("Firebase"," PlayersIDs $playersIDs")
                                            addStadiumRoomToFirestore(
                                                stadium!!,
                                                playersIDs,
                                                onSuccessListener = {
                                                    Log.e("Firebase"," Stadium Room Added Successfully ")

                                                    resetCounterAndRemovePlayers(
                                                        stadiumID = stadium!!.stadiumID!!,
                                                        onSuccessListener = {
                                                            Log.e("Firebase"," Document $it Removed Successfully ")

                                                        },
                                                        onFailureListener = {
                                                            Log.e("Firebase","Error Adding Stadium Room")
                                                        }
                                                    )//reset counter and delete documents
                                                },
                                                onFailureListener = { Log.e("Firebase"," Error Adding Stadium Room ") }
                                            )

                                        },
                                        onFailureListener = {
                                            Log.e("Firebase","Error getting PlayersIDs: ",it)

                                        }
                                    )// end get list of players id
                                }

                            },
                            onFailureListener = { Log.e("Firebase","Error getting players count: ",it) }
                        )// end check counter

                                        },
                    onFailureListener = { Log.e("Firebase","Error adding document and updating counter: ",it) }
                ) // end set player
            } },
        onFailureListener = {
                Log.e("Firebase"," Error adding player to search")
            }
        )// end check if player document exist


    }

}

