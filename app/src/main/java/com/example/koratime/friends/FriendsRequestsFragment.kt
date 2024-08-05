package com.example.koratime.friends

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.PendingFriendsAdapter
import com.example.koratime.basic.BasicFragment
import com.example.koratime.database.acceptFriendRequest
import com.example.koratime.database.checkIfFriendExist
import com.example.koratime.database.getFriendRequestsFromFirestore
import com.example.koratime.database.removeFriendRequestWithRequestID
import com.example.koratime.database.updateFriendshipStatus
import com.example.koratime.databinding.FragmentFriendsRequestsBinding
import com.example.koratime.friends.search.SearchActivity
import com.example.koratime.model.FriendRequestModel

class FriendsRequestsFragment :
    BasicFragment<FragmentFriendsRequestsBinding, FriendsRequestsViewModel>(),
    FriendsRequestsNavigator {
    private val adapter = PendingFriendsAdapter(null)


    override fun getLayoutID(): Int {
        return R.layout.fragment_friends_requests
    }

    override fun initViewModel(): FriendsRequestsViewModel {
        return ViewModelProvider(this)[FriendsRequestsViewModel::class.java]
    }

    override fun initView() {
        callback()
    }

    override fun callback() {
        viewModel.navigator = this
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = adapter
        }
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
                                    Toast.makeText(
                                        requireContext(),
                                        "${user.senderName} Added as a friends",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                                    Toast.makeText(
                                        requireContext(),
                                        "${user.senderName} Added as a friends",
                                        Toast.LENGTH_SHORT
                                    ).show()

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
                            Toast.makeText(
                                requireContext(),
                                "${user.senderName} Removed",
                                Toast.LENGTH_SHORT
                            ).show()
                            adapter.removeData(user)
                        },
                        onFailureListener = {
                            Log.e("Firebase: ", " Error removing request")

                        }
                    )
                }
            }
    }


    override fun onStart() {
        super.onStart()
        getRequests()

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
                Toast.makeText(
                    requireContext(),
                    "Error Loading Friend Requests",
                    Toast.LENGTH_SHORT
                ).show()
            }

        )
    }

    override fun openSearchActivity() {
        val intent = Intent(requireContext(), SearchActivity::class.java)
        startActivity(intent)
    }


}