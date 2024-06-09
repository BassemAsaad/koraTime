package com.example.koratime.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.database.checkFriendRequestStatusFromFirestore
import com.example.koratime.databinding.ItemAddFriendBinding
import com.example.koratime.model.UserModel


class AddFriendsAdapter  (private var usersList : List<UserModel?>?, private val currentUserId: String?)
    : RecyclerView.Adapter<AddFriendsAdapter.ViewHolder>()  {

     class ViewHolder(val dataBinding: ItemAddFriendBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun bind(user: UserModel) {
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = usersList!![position]!!
        holder.bind(user)

        checkFriendRequestStatusFromFirestore(currentUser= currentUserId!!, receiver =  user.id!!,
            onFailureListener = {
                Log.e("Firestore", "Error checking friend request status", it)
            }) { status ->
            Log.e("Firebase"," $status")
            Log.e("Firebase"," ${user.userName}")
            when (status) {
                "pending" -> {
                    holder.dataBinding.addFriendButtonItem.text = "Pending"
                    holder.dataBinding.addFriendButtonItem.isEnabled = false
                    holder.dataBinding.removeFriendButtonItem.isEnabled = true
                }
                "accepted" -> {
                    holder.dataBinding.addFriendButtonItem.text = "Friends"
                    holder.dataBinding.addFriendButtonItem.isEnabled = false
                    holder.dataBinding.removeFriendButtonItem.isEnabled = false
                }
                else -> {
                    holder.dataBinding.addFriendButtonItem.text = "Add Friend"
                    holder.dataBinding.addFriendButtonItem.isEnabled = true
                    holder.dataBinding.removeFriendButtonItem.isEnabled = false

                }

            }
        }


        holder.dataBinding.addFriendButtonItem.setOnClickListener {
            onAddFriendButtonClickListener?.onClick(user,holder, position)
        }
        holder.dataBinding.removeFriendButtonItem.setOnClickListener {
            onRemoveFriendButtonClickListener?.onClick(user,holder,position)
        }
    }



    var onAddFriendButtonClickListener:OnAddFriendButtonClickListener?=null
    interface OnAddFriendButtonClickListener{
        fun onClick(user : UserModel,holder: ViewHolder, position: Int)
    }

    var onRemoveFriendButtonClickListener:OnRemoveFriendButtonClickListener?=null
    interface OnRemoveFriendButtonClickListener{
        fun onClick(user : UserModel,holder: ViewHolder, position: Int)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun changeData(newUser : List<UserModel?>?){
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
            //check if the userName of each user contains the query string.
            originalUsersList?.forEach { user ->
                if (user?.userName?.contains(query, ignoreCase = true) == true) {
                    filteredList.add(user)
                }
            }
        }

        changeData(filteredList)
    }

}