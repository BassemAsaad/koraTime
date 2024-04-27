package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemReceiveMessageBinding
import com.example.koratime.databinding.ItemSendMessageBinding
import com.example.koratime.model.MessageModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RoomChatAdapter :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var item = mutableListOf<MessageModel?>()

    inner class SendMessageViewHolder (val itemSendBinding : ItemSendMessageBinding): RecyclerView.ViewHolder(itemSendBinding.root){
        fun bind(messageModel: MessageModel){
            itemSendBinding.messageModel = messageModel
            itemSendBinding.executePendingBindings()
        }
    }
    inner class ReceiveMessageViewHolder (val itemReceiveBinding: ItemReceiveMessageBinding): RecyclerView.ViewHolder(itemReceiveBinding.root){
        fun bind(messageModel: MessageModel){
            itemReceiveBinding.messageModel =  messageModel
            itemReceiveBinding.executePendingBindings()
        }
    }




    // to inflate receive and send viewHolders
    val RECEIVED = 1
    val SEND = 2
    override fun getItemViewType(position: Int): Int {
        val message = item.get(position)
        val user = Firebase.auth.currentUser?.uid
        if (message?.senderID==user){
            return SEND
        } else{
            return RECEIVED
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if ( holder is SendMessageViewHolder){
            holder.bind(item[position]!!)
        } else if ( holder is ReceiveMessageViewHolder){
            holder.bind(item[position]!!)

        }
    }


    fun changeData( newItem: MutableList<MessageModel?> ){
        item.addAll(newItem)
        notifyItemRangeInserted(item.size+1,newItem.size)
    }
}