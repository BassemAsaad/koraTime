package com.example.koratime.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.database.getLastMessageFromFirestore
import com.example.koratime.databinding.ItemFriendsBinding
import com.example.koratime.model.FriendModel

class FriendsAdapter  (var friendsList : List<FriendModel?>?): RecyclerView.Adapter<FriendsAdapter.ViewHolder>()  {
    class ViewHolder(val dataBinding : ItemFriendsBinding): RecyclerView.ViewHolder(dataBinding.root){
        fun bind(friend : FriendModel){
            dataBinding.friendModel = friend
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
        return friendsList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(friendsList!![position]!!)


        getLastMessageFromFirestore(
            userID = DataUtils.user!!.id!!,
            friendshipID = holder.dataBinding.friendModel!!.friendshipID!!,
            onSuccessListener = {lastMessage->
                holder.dataBinding.lastMessageItem.text = lastMessage
                Log.e("Firebase ","Message returned successfully")
            },
            onFailureListener = {
                Log.e("Firebase ","Error returning message")
            }
        )
        holder.itemView.setOnClickListener {
            onUserClickListener?.onItemClick(friendsList!![position]!!,holder,position)
        }

    }
    var onUserClickListener : OnUserClickListener?=null
    interface OnUserClickListener{
        fun onItemClick(user : FriendModel?,holder: ViewHolder, position: Int)
    }
    fun changeData( newFriend : List<FriendModel?>?){
        friendsList = newFriend
        notifyDataSetChanged()
    }

}