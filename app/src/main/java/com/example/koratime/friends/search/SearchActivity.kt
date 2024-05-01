package com.example.koratime.friends.search

import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.AddFriendsAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.addFriendRequestToFirestore
import com.example.koratime.database.getUsersFromFirestore
import com.example.koratime.database.removeFriendRequestFromFirestoreWithoutRequestID
import com.example.koratime.databinding.ActivitySearchBinding
import com.example.koratime.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Suppress("DEPRECATION")
class SearchActivity : BasicActivity<ActivitySearchBinding,SearchViewModel>(),SearchNavigator {
    private val usersList = mutableListOf<UserModel?>()
    val currentUserId= Firebase.auth.currentUser?.uid
    private val currentUserPicture= DataUtils.user!!.profilePicture
    private val currentUserName= DataUtils.user!!.userName
    private val adapter = AddFriendsAdapter(usersList,currentUserId )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_search
    }

    override fun initViewModel(): SearchViewModel {
        return ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator = this

        dataBinding.recyclerView.adapter = adapter

        setSupportActionBar(dataBinding.toolbar)
        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title= "Search For People"
        supportActionBar?.setDisplayShowTitleEnabled(true)
        dataBinding.searchUser.requestFocus()


        dataBinding.searchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Handle query submission if needed
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filterUsers(newText)
                return true
            }
        })
        // send friend request
        adapter.onAddFriendButtonClickListener=object : AddFriendsAdapter.OnAddFriendButtonClickListener{
            override fun onClick(
                user : UserModel,
                holder: AddFriendsAdapter.ViewHolder,
                position: Int
            ) {
                val receiverUserId = user.id
                    addFriendRequestToFirestore(
                        sender = currentUserId!!,
                        receiver = receiverUserId!!,
                        senderPicture = currentUserPicture,
                        senderUserName = currentUserName,
                        onSuccessListener = {
                            holder.dataBinding.addFriendButtonItem.text= "Pending"
                            holder.dataBinding.addFriendButtonItem.isEnabled = false
                            Log.e("Firebase", "Friend request sent to: $receiverUserId" )
                        },
                        onFailureListener = { e ->
                            Log.e("Firebase", "Error sending friend request: ", e)
                        }
                    )


            }
        }

        //remove friend request
        adapter.onRemoveFriendButtonClickListener = object :AddFriendsAdapter.OnRemoveFriendButtonClickListener{
            override fun onClick(
                user: UserModel,
                holder: AddFriendsAdapter.ViewHolder,
                position: Int
            ) {
                val receiverUserId = user.id
                    removeFriendRequestFromFirestoreWithoutRequestID(
                        sender = currentUserId!!,
                        receiver = receiverUserId!!,
                        onSuccessListener = {
                            holder.dataBinding.addFriendButtonItem.text= "Add Friend"
                            holder.dataBinding.addFriendButtonItem.isEnabled = true
                            Log.e("firebase","Friend request removed")
                        },
                        onFailureListener = {e->
                            Log.e("firebase","error removing friend request: ",e)

                        }
                    )

            }
        }

    }

    override fun onStart() {
        super.onStart()
        getUsersFromFirestore(
            currentUserId,
            onSuccessListener = { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val user = document.toObject(UserModel::class.java)
                    usersList.add(user)
                }
                adapter.changeData(usersList)
                Log.e("Firebase"," Data has been added to adapter successfully ")
            },
            onFailureListener = {e->
                Log.e("Firebase"," Error adding Data to adapter: ",e)
            }
        )

    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked
        onBackPressed()
        return true
    }

}