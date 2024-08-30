package com.example.koratime.friends.search

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.example.koratime.adapters.AddFriendsAdapter
import com.example.koratime.adapters.parentAdapters.FindFriendsAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.model.UserModel
import com.example.koratime.utils.DataUtils
import com.example.koratime.utils.addFriendRequestToFirestore
import com.example.koratime.utils.getUsersFromFirestore
import com.example.koratime.utils.removeFriendRequestWithoutRequestID
import com.google.firebase.firestore.DocumentChange

@SuppressLint("SetTextI18n")
class FindFriendsViewModel : BasicViewModel<FindFriendsNavigator>() {
    override val TAG: String
        get() = FindFriendsViewModel::class.java.simpleName
    private val usersList = mutableListOf<UserModel?>()
    private val addFriendsAdapter = AddFriendsAdapter(emptyList())
    lateinit var findFriendsAdapter: FindFriendsAdapter
    val toastMessage = MutableLiveData<String>()

    fun adapterSetup() {
        getUsers()
        findFriendsAdapter = FindFriendsAdapter(addFriendsAdapter)

    }

    fun adapterCallback() {
        // send friend request
        addFriendsAdapter.onButtonClickListener = object : AddFriendsAdapter.OnButtonClickListener {
            override fun onAddFriendClickListener(
                user: UserModel,
                holder: AddFriendsAdapter.ViewHolder,
                position: Int
            ) {
                addFriendRequestToFirestore(
                    currentUser = DataUtils.user!!,
                    receiver = user,
                    onSuccessListener = {
                        holder.dataBinding.apply {
                            addFriendButtonItem.text = "Pending"
                            addFriendButtonItem.isEnabled = false
                            removeFriendButtonItem.isEnabled = true
                        }
                        log("Friend request sent to: ${user.id}")
                    },
                    onFailureListener = { e ->
                        log("Error sending friend request $e")
                    }
                )

            }

            override fun onRemoveFriendClickListener(
                user: UserModel,
                holder: AddFriendsAdapter.ViewHolder,
                position: Int
            ) {
                removeFriendRequestWithoutRequestID(
                    sender = DataUtils.user!!.id!!,
                    receiver = user.id!!,
                    onSuccessListener = {
                        holder.dataBinding.apply {
                            addFriendButtonItem.text = "Add Friend"
                            addFriendButtonItem.isEnabled = true
                            removeFriendButtonItem.isEnabled = false

                        }
                        log("Friend request removed")
                    },
                    onFailureListener = { e ->
                        log("Error removing friend request: $e")

                    }
                )
            }
        }
    }

    private fun getUsers() {
        getUsersFromFirestore(DataUtils.user!!.id)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    log("Error getting users: $error")
                    toastMessage.value = "Error getting users"
                } else {
                    for (doc in snapshots!!.documentChanges) {
                        when (doc.type) {
                            DocumentChange.Type.ADDED -> {
                                val user = doc.document.toObject(UserModel::class.java)
                                usersList.add(user)
                            }

                            DocumentChange.Type.MODIFIED -> log("Modified")

                            DocumentChange.Type.REMOVED -> {
                                val user = doc.document.toObject(UserModel::class.java)
                                usersList.remove(user)
                            }
                        }
                    }
                    addFriendsAdapter.changeData(usersList)
                }
            }
    }
}