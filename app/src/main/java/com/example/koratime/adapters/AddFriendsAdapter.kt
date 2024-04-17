package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemAcceptFriendBinding
import com.example.koratime.databinding.ItemAddFriendBinding
import com.example.koratime.databinding.ItemFriendsBinding
import com.example.koratime.model.RoomModel
import com.example.koratime.model.UserModel

class AddFriendsAdapter  (var usersList : List<UserModel?>?): RecyclerView.Adapter<AddFriendsAdapter.ViewHolder>()  {
    class ViewHolder(val dataBinding : ItemAddFriendBinding): RecyclerView.ViewHolder(dataBinding.root){
        fun bind(user : UserModel){
            dataBinding.vm = user
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
        holder.bind(usersList!![position]!!)


    }

    fun changeData( newUser : List<UserModel?>?){
        usersList = newUser
        notifyDataSetChanged()
    }

}