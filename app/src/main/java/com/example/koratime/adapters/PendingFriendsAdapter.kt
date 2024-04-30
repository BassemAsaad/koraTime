package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemPendingFriendBinding
import com.example.koratime.model.FriendModel

class PendingFriendsAdapter  (var usersList : List<FriendModel?>?): RecyclerView.Adapter<PendingFriendsAdapter.ViewHolder>()  {
    inner class ViewHolder(val dataBinding : ItemPendingFriendBinding): RecyclerView.ViewHolder(dataBinding.root){
        fun bind(friendModel : FriendModel){
            dataBinding.friendModel = friendModel
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
        holder.bind(usersList!![position]!!)


    }

    fun changeData( newUser : List<FriendModel?>?){
        usersList = newUser
        notifyDataSetChanged()
    }

}