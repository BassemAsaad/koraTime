package com.example.koratime.friends.search

import android.annotation.SuppressLint
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
import com.example.koratime.database.removeFriendRequestWithoutRequestID
import com.example.koratime.databinding.ActivitySearchBinding
import com.example.koratime.model.UserModel



@Suppress("DEPRECATION")
class SearchActivity : BasicActivity<ActivitySearchBinding,SearchViewModel>(),SearchNavigator {
    private val usersList = mutableListOf<UserModel?>()
    private val currentUserPicture= DataUtils.user!!.profilePicture
    private val currentUserName= DataUtils.user!!.userName
    private val adapter = AddFriendsAdapter(usersList,DataUtils.user!!.id )
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

        // filter users for search
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
            @SuppressLint("SuspiciousIndentation")
            override fun onClick(
                user : UserModel,
                holder: AddFriendsAdapter.ViewHolder,
                position: Int
            ) {
                val receiverUserId = user.id
                    addFriendRequestToFirestore(
                        sender = DataUtils.user!!.id!!,
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
            @SuppressLint("SuspiciousIndentation")
            override fun onClick(
                user: UserModel,
                holder: AddFriendsAdapter.ViewHolder,
                position: Int
            ) {
                val receiverUserId = user.id!!
                    removeFriendRequestWithoutRequestID(
                        sender = DataUtils.user!!.id!!,
                        receiver = receiverUserId,
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
            DataUtils.user!!.id,
            onSuccessListener = { querySnapshot ->
                for (doc in querySnapshot.documents){
                    val user = doc.toObject(UserModel::class.java)
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