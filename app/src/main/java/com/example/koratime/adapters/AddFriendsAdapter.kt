package com.example.koratime.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.database.addFriendFromFirestore
import com.example.koratime.databinding.ItemAddFriendBinding
import com.example.koratime.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class AddFriendsAdapter  (private var usersList : List<UserModel?>?): RecyclerView.Adapter<AddFriendsAdapter.ViewHolder>()  {
    class ViewHolder(val dataBinding : ItemAddFriendBinding): RecyclerView.ViewHolder(dataBinding.root){
        fun bind(user : UserModel){
            dataBinding.userModel = user
            dataBinding.invalidateAll()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding : ItemAddFriendBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context)
                , R.layout.item_add_friend, parent,false)

        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return usersList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = usersList!![position]!!
        holder.bind(user)
        holder.dataBinding.addFriendButtonItem.setOnClickListener {
            val currentUserId= Firebase.auth.currentUser?.uid
            val recipientUserId = usersList!![position]?.id

            if (currentUserId != null && recipientUserId != null) {
                addFriendFromFirestore(
                    from = currentUserId,
                    to = recipientUserId,
                    onSuccessListener = { documentReference ->
                        holder.dataBinding.addFriendButtonItem.text= "Pending"
                        holder.dataBinding.addFriendButtonItem.isEnabled = false
                        Log.e("Firebase", "Friend request sent with ID: ${documentReference.id}")
                    },
                    onFailureListener = { e ->
                        Log.e("Firebase", "Error sending friend request", e)
                    }
                )
            } else {
                Log.e("Firebase", "Current user ID or recipient user ID is null")
            }
        }




    }

    fun changeData( newUser : List<UserModel?>?){
        usersList = newUser
        notifyDataSetChanged()
    }
    private var originalUsersList: List<UserModel?>? = null
    fun filterUsers(query: String) {
        if (originalUsersList == null) {
            originalUsersList = usersList
        }

        val filteredList = mutableListOf<UserModel?>()

        if (query.isEmpty()) {
            filteredList.addAll(originalUsersList ?: emptyList())
        } else {
            originalUsersList?.forEach { user ->
                if (user?.userName?.contains(query, ignoreCase = true) == true) {
                    filteredList.add(user)
                }
            }
        }

        changeData(filteredList)
    }

}