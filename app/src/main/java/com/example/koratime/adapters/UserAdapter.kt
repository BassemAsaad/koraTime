package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemFriendsBinding
import com.example.koratime.model.RoomModel
import com.example.koratime.model.UserModel

class UserAdapter  (var usersList : List<UserModel?>?): RecyclerView.Adapter<UserAdapter.ViewHolder>()  {
    class ViewHolder(val dataBinding : ItemFriendsBinding): RecyclerView.ViewHolder(dataBinding.root){
        fun bind(user : UserModel){
            dataBinding.vm = user
            dataBinding.invalidateAll()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding : ItemFriendsBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context)
                , R.layout.item_friends, parent,false)

        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return usersList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(usersList!![position]!!)
        holder.itemView.setOnClickListener {
            onUserClickListener?.onItemClick(usersList!![position]!!,position)
        }

    }
    var onUserClickListener : OnUserClickListener?=null
    interface OnUserClickListener{
        fun onItemClick(user : UserModel?, position: Int)
    }
    fun changeData( newUser : List<UserModel?>?){
        usersList = newUser
        notifyDataSetChanged()
    }

}