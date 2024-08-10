package com.example.koratime.chat

import android.content.Intent
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.FriendsAdapter
import com.example.koratime.basic.BasicFragment
import com.example.koratime.chat.chat_friends.ChatFriendsActivity
import com.example.koratime.database.getFriendsFromFirestore
import com.example.koratime.database.removeFriendFromFirestore
import com.example.koratime.databinding.FragmentChatBinding
import com.example.koratime.model.FriendModel


class ChatFragment : BasicFragment<FragmentChatBinding, ChatViewModel>(), ChatNavigator {
    val adapter = FriendsAdapter(null)
    private lateinit var friendsList: MutableList<FriendModel>

    override fun initViewModel(): ChatViewModel {
        return ViewModelProvider(this)[ChatViewModel::class.java]
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_chat
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
        adapter.onUserClickListener = object : FriendsAdapter.OnUserClickListener {
            override fun onItemClick(
                user: FriendModel?,
                holder: FriendsAdapter.ViewHolder,
                position: Int
            ) {
                val intent = Intent(requireContext(), ChatFriendsActivity::class.java)
                intent.putExtra(Constants.FRIEND, user)
                startActivity(intent)

            }


        }
        /*
        override fun onRemoveClick(
                user: FriendModel?,
                holder: FriendsAdapter.ViewHolder,
                position: Int
            ) {
                removeFriendFromFirestore(
                    user1 = DataUtils.user!!,
                    user2 = user!!,
                    onSuccessListener = {
                        friendsList.remove(user)
                        adapter.changeData(friendsList)
                        Log.e("Firebase", "Friend Removed Successfully")
                    },
                    onFailureListener = {
                        Log.e("Firebase", "Friend not removed ")
                    }
                )
            }
         */

        // filter users for search
        dataBinding.searchFriends.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    private fun getFriends() {
        getFriendsFromFirestore(
            DataUtils.user!!.id!!,
            onSuccessListener = { querySnapshot ->
                friendsList = querySnapshot.toObjects(FriendModel::class.java)
                adapter.changeData(friendsList)
            },
            onFailureListener = { e ->
                Log.e("Firebase", " Error loading friends", e)
            }
        )
    }

    override fun onStart() {
        super.onStart()
        getFriends()
    }

}