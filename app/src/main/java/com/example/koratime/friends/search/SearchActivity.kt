package com.example.koratime.friends.search

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.utils.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.AddFriendsAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.utils.addFriendRequestToFirestore
import com.example.koratime.utils.getUsersFromFirestore
import com.example.koratime.utils.removeFriendRequestWithoutRequestID
import com.example.koratime.databinding.ActivitySearchBinding
import com.example.koratime.model.UserModel


@Suppress("DEPRECATION", "SetTextI18n")
class SearchActivity : BasicActivity<ActivitySearchBinding, SearchViewModel>(), SearchNavigator {
    override val TAG: String
        get() = "SearchActivity"
    private val usersList = mutableListOf<UserModel?>()
    private val adapter = AddFriendsAdapter(usersList, DataUtils.user!!.id)

    override fun getLayoutID(): Int {
        return R.layout.activity_search
    }

    override fun initViewModel(): SearchViewModel {
        return ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun initView() {
        setSupportActionBar(dataBinding.toolbar)
        callback()

    }

    override fun callback() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "Search For People"
        }
        dataBinding.apply {
            viewModel.navigator = this@SearchActivity
            vm = viewModel
            recyclerView.adapter = adapter
//            searchUser.requestFocus()
            // filter users for search
            searchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    // Handle query submission if needed
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    adapter.filterUsers(newText)
                    return true
                }
            })
        }

        // send friend request
        adapter.onAddFriendButtonClickListener =
            object : AddFriendsAdapter.OnAddFriendButtonClickListener {
                override fun onClick(
                    user: UserModel,
                    holder: AddFriendsAdapter.ViewHolder,
                    position: Int
                ) {
                    addFriendRequestToFirestore(
                        sender = DataUtils.user!!,
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
            }

        //remove friend request
        adapter.onRemoveFriendButtonClickListener =
            object : AddFriendsAdapter.OnRemoveFriendButtonClickListener {
                override fun onClick(
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

    override fun onStart() {
        super.onStart()
        getUsers()

    }

    private fun getUsers() {
        getUsersFromFirestore(
            DataUtils.user!!.id,
            onSuccessListener = { querySnapshot ->
                for (doc in querySnapshot.documents) {
                    val user = doc.toObject(UserModel::class.java)
                    usersList.add(user)
                }
                adapter.changeData(usersList)
                log("Data has been added to adapter successfully")
            },
            onFailureListener = { e ->
                log("Error adding Data to adapter: $e")
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked
        onBackPressed()
        return true
    }

}