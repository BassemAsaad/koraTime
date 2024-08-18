package com.example.koratime.chat

import android.util.Log
import com.example.koratime.DataUtils
import com.example.koratime.adapters.FriendsAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.getFriendsFromFirestore
import com.example.koratime.model.FriendModel

class ChatViewModel : BasicViewModel<ChatNavigator>() {
    override val TAG: String
        get() = ChatViewModel::class.java.simpleName
    val adapter = FriendsAdapter(null)

    fun adapterSetup() {
        getFriends()
    }

    fun adapterCallback() {
        adapter.onUserClickListener = object : FriendsAdapter.OnUserClickListener {
            override fun onItemClick(
                user: FriendModel?,
                holder: FriendsAdapter.ViewHolder,
                position: Int
            ) {
                navigator?.openChatFriendsActivity(user)

            }
        }
    }

    private fun getFriends() {
        getFriendsFromFirestore(
            DataUtils.user!!.id!!,
            onSuccessListener = { querySnapshot ->
                val friendsList = querySnapshot.toObjects(FriendModel::class.java)
                adapter.changeData(friendsList)
            },
            onFailureListener = { e ->
                Log.e("Firebase", " Error loading friends", e)
            }
        )
    }
}