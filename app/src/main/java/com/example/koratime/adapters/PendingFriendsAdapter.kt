package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemPendingFriendBinding
import com.example.koratime.model.FriendRequestModel

class PendingFriendsAdapter  (var usersList : MutableList<FriendRequestModel?>?): RecyclerView.Adapter<PendingFriendsAdapter.ViewHolder>()  {
    inner class ViewHolder(val dataBinding : ItemPendingFriendBinding): RecyclerView.ViewHolder(dataBinding.root){
        fun bind(friendRequestModel : FriendRequestModel){
            dataBinding.friendModel = friendRequestModel
            dataBinding.invalidateAll()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding : ItemPendingFriendBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context)
                , R.layout.item_pending_friend, parent,false)

        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return usersList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user= usersList!![position]!!
        holder.bind(user)
        holder.dataBinding.confirmFriendButtonItem.setOnClickListener {
            onAddButtonClickListener?.onClick(user,holder,position)
        }
        holder.dataBinding.removeFriendButtonItem.setOnClickListener {
            onRemoveButtonClickListener?.onClick(user,holder,position)
        }

    }

    var onRemoveButtonClickListener :OnRemoveButtonClickListener?=null
    interface OnRemoveButtonClickListener{
        fun onClick(user:FriendRequestModel, holder: ViewHolder, position: Int)
    }

    var onAddButtonClickListener :OnAddButtonClickListener?=null
    interface OnAddButtonClickListener{
        fun onClick(user:FriendRequestModel, holder: ViewHolder, position: Int)
    }

    fun changeData( newUser : MutableList<FriendRequestModel?>?){
        usersList = newUser
        notifyDataSetChanged()
    }

    fun removeData( newUser : FriendRequestModel){
        usersList!!.remove(newUser)
        notifyDataSetChanged()
    }
}