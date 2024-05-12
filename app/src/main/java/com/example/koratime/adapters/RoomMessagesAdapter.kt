package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.databinding.ItemReceiveMessageBinding
import com.example.koratime.databinding.ItemSendMessageBinding
import com.example.koratime.model.FriendMessageModel
import com.example.koratime.model.RoomMessageModel

class RoomMessagesAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var item = mutableListOf<RoomMessageModel?>()



    inner class SendMessageViewHolder (private val itemSendMessageBinding: ItemSendMessageBinding)
        :ViewHolder (itemSendMessageBinding.root){
            fun bind (roomMessageModel: RoomMessageModel){
                itemSendMessageBinding.messageModel = roomMessageModel
                itemSendMessageBinding.executePendingBindings()
            }
        }

    inner class ReceiveMessageViewHolder (private val receiveMessageBinding: ItemReceiveMessageBinding)
        :ViewHolder (receiveMessageBinding.root){
        fun bind (roomMessageModel: RoomMessageModel){
            receiveMessageBinding.messageModel = roomMessageModel
            receiveMessageBinding.executePendingBindings()
        }
    }

    companion object{
        private const val RECEIVED = 1
        private const val SEND = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = item[position]

        if (message?.senderID==DataUtils.user?.id){
            return SEND
        } else{
            return RECEIVED
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType==RECEIVED){
            val itemBinding : ItemReceiveMessageBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_receive_message, parent, false,)
            return ReceiveMessageViewHolder(itemBinding)
        } else {
            val itemBinding : ItemSendMessageBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_send_message, parent, false,)
            return SendMessageViewHolder(itemBinding)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if ( holder is SendMessageViewHolder){
            holder.bind(item[position]!!)
        } else if ( holder is ReceiveMessageViewHolder){
            holder.bind(item[position]!!)

        }
    }

    fun changeData(newItem: List<RoomMessageModel?>) {
        //  position the new items
        val positionStart = item.size
        if (positionStart == 0) {
            item.clear()  // Clear existing items only if necessary
        }
        item.addAll(newItem)
        notifyItemRangeInserted(positionStart, newItem.size)
    }

}