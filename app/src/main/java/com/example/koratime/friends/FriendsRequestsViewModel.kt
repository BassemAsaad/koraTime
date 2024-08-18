package com.example.koratime.friends

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.koratime.DataUtils
import com.example.koratime.adapters.PendingFriendsAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.acceptFriendRequest
import com.example.koratime.database.checkIfFriendExist
import com.example.koratime.database.getFriendRequestsFromFirestore
import com.example.koratime.database.removeFriendRequestWithRequestID
import com.example.koratime.database.updateFriendshipStatus
import com.example.koratime.model.FriendRequestModel

class FriendsRequestsViewModel : BasicViewModel<FriendsRequestsNavigator>() {
    override val TAG: String
        get() = FriendsRequestsViewModel::class.java.simpleName
    val adapter = PendingFriendsAdapter(null)
    val toastMessage = MutableLiveData<String>()


    fun adapterSetup() {
        getRequests()
    }

    fun adapterCallback() {
        adapter.onAddButtonClickListener = object : PendingFriendsAdapter.OnAddButtonClickListener {
            @SuppressLint("SetTextI18n")
            override fun onClick(
                user: FriendRequestModel,
                holder: PendingFriendsAdapter.ViewHolder,
                position: Int
            ) {
                checkIfFriendExist(
                    currentUser = DataUtils.user!!,
                    userSender = user,
                    onSuccessListener = {
                        Log.e("Firebase", " ${user.senderName} ex friend status $it")
                        if (it) {
                            updateFriendshipStatus(
                                currentUser = DataUtils.user!!,
                                sender = user,
                                onSuccessListener = {
                                    Log.e(
                                        "Firebase",
                                        " ${user.senderName.toString()} Updated successfully as a friend"
                                    )
                                    holder.dataBinding.confirmFriendButtonItem.text = "Friends"
                                    holder.dataBinding.confirmFriendButtonItem.isEnabled = false
                                    toastMessage.value = "${user.senderName} Added as a friends"
                                },
                                onFailureListener = { e ->
                                    Log.e(
                                        "Firebase",
                                        " Error updating ${user.senderName} to be friend... Error: ",
                                        e
                                    )
                                }


                            )
                        } else {
                            acceptFriendRequest(
                                sender = user,
                                receiver = DataUtils.user!!,
                                requestID = user.requestID!!,
                                onSuccessListener = {
                                    Log.e(
                                        "Firebase",
                                        " ${user.senderName.toString()} Accepted Successfully "
                                    )
                                    holder.dataBinding.confirmFriendButtonItem.text = "Friends"
                                    holder.dataBinding.confirmFriendButtonItem.isEnabled = false
                                    toastMessage.value = "${user.senderName} Added as a friends"

                                },
                                onFailureListener = {
                                    Log.e("Firebase", " Error Accepting  ${user.senderName} ")
                                }
                            )
                        }
                    },
                    onFailureListener = {
                        Log.e("Firebase", " Error checking if ${user.senderName} is an ex friend ")
                    }

                )


            }
        }
        adapter.onRemoveButtonClickListener =
            object : PendingFriendsAdapter.OnRemoveButtonClickListener {
                override fun onClick(
                    user: FriendRequestModel,
                    holder: PendingFriendsAdapter.ViewHolder,
                    position: Int
                ) {
                    val requestId = user.requestID
                    val senderID = user.senderID
                    val receiverID = DataUtils.user!!.id
                    removeFriendRequestWithRequestID(
                        sender = senderID!!,
                        receiver = receiverID!!,
                        request = requestId!!,
                        onSuccessListener = {
                            Log.e("Firebase: ", " Request has been removed successfully $requestId")
                            toastMessage.value = "${user.senderName} Removed"
                            adapter.removeData(user)
                        },
                        onFailureListener = {
                            Log.e("Firebase: ", " Error removing request")

                        }
                    )
                }
            }
    }

    private fun getRequests() {
        getFriendRequestsFromFirestore(
            DataUtils.user!!,
            onSuccessListener = { querySnapshot ->
                val user = querySnapshot.toObjects(FriendRequestModel::class.java)
                adapter.changeData(user)
                Log.e("Firebase: ", " Friend requests loaded successfully")

            },
            onFailureListener = { e ->
                Log.e("Firebase: ", " Error loading friend requests: ", e)
                toastMessage.value = "Error Loading Friend Requests"
            }

        )
    }

    fun openSearch() {
        navigator?.openSearchActivity()
    }


}