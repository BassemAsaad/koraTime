package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.databinding.ItemFriendReceiveMessageBinding
import com.example.koratime.databinding.ItemFriendSendMessageBinding
import com.example.koratime.model.FriendMessageModel

class FriendMessagesAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var item = mutableListOf<FriendMessageModel?>()


    inner class SendMessageViewHolder(private val itemSendMessageBinding: ItemFriendSendMessageBinding) :
        ViewHolder(itemSendMessageBinding.root) {
        fun bind(friendMessageModel: FriendMessageModel) {
            itemSendMessageBinding.messageModel = friendMessageModel
            itemSendMessageBinding.executePendingBindings()
        }
    }

    inner class ReceiveMessageViewHolder(private val receiveMessageBinding: ItemFriendReceiveMessageBinding) :
        ViewHolder(receiveMessageBinding.root) {
        fun bind(friendMessageModel: FriendMessageModel) {
            receiveMessageBinding.messageModel = friendMessageModel
            receiveMessageBinding.executePendingBindings()
        }
    }

    companion object {
        private const val RECEIVED = 1
        private const val SEND = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = item[position]

        if (message?.senderID == DataUtils.user?.id) {
            return SEND
        } else {
            return RECEIVED
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == RECEIVED) {
            val itemBinding: ItemFriendReceiveMessageBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_friend_receive_message, parent, false,
            )
            return ReceiveMessageViewHolder(itemBinding)
        } else {
            val itemBinding: ItemFriendSendMessageBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_friend_send_message, parent, false,
            )
            return SendMessageViewHolder(itemBinding)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is SendMessageViewHolder) {
            holder.bind(item[position]!!)
        } else if (holder is ReceiveMessageViewHolder) {
            holder.bind(item[position]!!)

        }
    }

    fun changeData(newItem: List<FriendMessageModel?>) {
        // Update this to clear the existing items and add all the new items
        val positionStart = item.size
        if (positionStart == 0) {
            item.clear()  // Clear existing items only if necessary
        }
        item.addAll(newItem)
        notifyItemRangeInserted(positionStart, newItem.size)
    }

}