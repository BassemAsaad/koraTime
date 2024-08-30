package com.example.koratime.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.utils.checkFriendRequestStatusFromFirestore
import com.example.koratime.databinding.ItemAddFriendBinding
import com.example.koratime.model.UserModel
import com.example.koratime.utils.DataUtils


class AddFriendsAdapter(private var usersList: List<UserModel?>?) :
    RecyclerView.Adapter<AddFriendsAdapter.ViewHolder>() {

    class ViewHolder(val dataBinding: ItemAddFriendBinding) :
        RecyclerView.ViewHolder(dataBinding.root) {
        fun bind(user: UserModel) {
            dataBinding.userModel = user
            dataBinding.invalidateAll()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding: ItemAddFriendBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_add_friend, parent, false
            )

        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return usersList?.size ?: 0
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = usersList!![position]!!
        holder.bind(user)

        checkFriendRequestStatusFromFirestore(
            currentUser = DataUtils.user!!.id!!,
            receiver = user.id!!,
            onSuccessListener = {status ->
                Log.e("Firebase", " ${user.userName} status: $status")
                when (status) {
                    "pending" -> {
                        holder.dataBinding.apply {
                            addFriendButtonItem.text = "Pending"
                            addFriendButtonItem.isEnabled = false
                            removeFriendButtonItem.isEnabled = true
                        }

                    }
                    "accepted" -> {
                        holder.dataBinding.apply {
                            addFriendButtonItem.text = "Friends"
                            addFriendButtonItem.isEnabled = false
                            removeFriendButtonItem.isEnabled = false
                        }
                    }

                    else -> {
                        holder.dataBinding.apply {
                            addFriendButtonItem.text = "Add Friend"
                            addFriendButtonItem.isEnabled = true
                            removeFriendButtonItem.isEnabled = false
                        }
                    }
                }
            },
            onFailureListener = { Log.e("Firestore", "Error checking friend request status", it) }
        )

        holder.dataBinding.addFriendButtonItem.setOnClickListener {
            onButtonClickListener?.onAddFriendClickListener(user, holder, position)
        }
        holder.dataBinding.removeFriendButtonItem.setOnClickListener {
            onButtonClickListener?.onRemoveFriendClickListener(user, holder, position)
        }
    }


    var onButtonClickListener: OnButtonClickListener? = null

    interface OnButtonClickListener {
        fun onAddFriendClickListener(user: UserModel, holder: ViewHolder, position: Int)
        fun onRemoveFriendClickListener(user: UserModel, holder: ViewHolder, position: Int)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeData(newUser: List<UserModel?>?) {
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