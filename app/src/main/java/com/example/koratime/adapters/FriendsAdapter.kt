package com.example.koratime.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.database.getLastMessageFromFirestore
import com.example.koratime.databinding.ItemFriendsBinding
import com.example.koratime.model.FriendMessageModel
import com.example.koratime.model.FriendModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FriendsAdapter(var friendsList: List<FriendModel?>?) :
    RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    class ViewHolder(val dataBinding: ItemFriendsBinding) :
        RecyclerView.ViewHolder(dataBinding.root) {
        fun bind(friend: FriendModel) {
            dataBinding.friendModel = friend
            dataBinding.invalidateAll()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding: ItemFriendsBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_friends, parent, false
            )

        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return friendsList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(friendsList!![position]!!)


        getLastMessageFromFirestore(
            userID = DataUtils.user!!.id!!,
            friendshipID = holder.dataBinding.friendModel!!.friendshipID!!,
            onSuccessListener = { documents ->
                var message = FriendMessageModel()
                for (document in documents) {
                    message = document.toObject(FriendMessageModel::class.java)
                    // Access the last content sent
                }
                holder.dataBinding.apply {
                    lastMessageItem.text = message.content
                    lastMessageTime.text = formatMessageTime(message.dateTime!!)
                }

                Log.e("Firebase ", "Message returned successfully")
            },
            onFailureListener = {
                Log.e("Firebase ", "Error returning message")
            }
        )
        holder.itemView.setOnClickListener {
                onUserClickListener?.onItemClick(friendsList!![position]!!, holder, position)
        }

    }

    var onUserClickListener: OnUserClickListener? = null

    interface OnUserClickListener {
        fun onItemClick(user: FriendModel?, holder: ViewHolder, position: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeData(newFriend: List<FriendModel?>?) {
        friendsList = newFriend
        notifyDataSetChanged()
    }

    private var originalUsersList: List<FriendModel?>? = null
    fun filterUsers(query: String) {
        if (originalUsersList == null) {
            originalUsersList = friendsList
        }

        val filteredList = mutableListOf<FriendModel?>()

        if (query.isEmpty()) {
            filteredList.addAll(originalUsersList ?: emptyList())
        } else {
            originalUsersList?.forEach { friend ->
                if (friend?.friendName?.contains(query, ignoreCase = true) == true) {
                    filteredList.add(friend)
                }
            }
        }

        changeData(filteredList)
    }
    private fun formatMessageTime(timestamp: Long): String {
        val currentTime = Calendar.getInstance()
        val messageTime = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        val isToday = currentTime.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                currentTime.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR)

        val isYesterday = currentTime.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                currentTime.get(Calendar.DAY_OF_YEAR) - messageTime.get(Calendar.DAY_OF_YEAR) == 1

        return when {
            isToday -> {
                // If the message is from today, show time
                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(messageTime.time)
            }
            isYesterday -> {
                // If the message is from yesterday, show 'Yesterday'
                "Yesterday"
            }
            else -> {
                // Otherwise, show the date
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(messageTime.time)
            }
        }
    }
}