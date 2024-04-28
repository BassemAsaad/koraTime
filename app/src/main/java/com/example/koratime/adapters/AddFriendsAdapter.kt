package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.database.checkFriendRequestStatus
import com.example.koratime.databinding.ItemAddFriendBinding
import com.example.koratime.model.UserModel


class AddFriendsAdapter  (private var usersList : List<UserModel?>?, private val currentUserId: String?)
    : RecyclerView.Adapter<AddFriendsAdapter.ViewHolder>()  {


    class ViewHolder(val dataBinding: ItemAddFriendBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun bind(user: UserModel, currentUserId: String?) {
            dataBinding.userModel = user
            dataBinding.invalidateAll()

            // Check friend request status dynamically
            currentUserId?.let { userId ->
                checkFriendRequestStatus(userId, user.id!!) { status ->
                    if (status == "pending") {
                        dataBinding.addFriendButtonItem.text = "Pending"
                        dataBinding.addFriendButtonItem.isEnabled = false
                    } else {
                        dataBinding.addFriendButtonItem.text = "Add Friend"
                        dataBinding.addFriendButtonItem.isEnabled = true
                    }
                }
            }



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
        holder.bind(user, currentUserId)


        holder.dataBinding.addFriendButtonItem.setOnClickListener {
            onAddClickListener?.onClick(user,holder, position)
        }


    }


    var onAddClickListener:OnAddClickListener?=null
    interface OnAddClickListener{
        fun onClick(user : UserModel,holder: ViewHolder, position: Int)
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