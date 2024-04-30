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
import com.example.koratime.model.MessageModel

class MessagesAdapter : RecyclerView.Adapter<ViewHolder>() {

    var item = mutableListOf<MessageModel?>()



    inner class SendMessageViewHolder (val itemSendMessageBinding: ItemSendMessageBinding)
        :ViewHolder (itemSendMessageBinding.root){
            fun bind (messageModel: MessageModel){
                itemSendMessageBinding.messageModel = messageModel
                itemSendMessageBinding.executePendingBindings()
            }
        }

    inner class ReceiveMessageViewHolder (val receiveMessageBinding: ItemReceiveMessageBinding)
        :ViewHolder (receiveMessageBinding.root){
        fun bind (messageModel: MessageModel){
            receiveMessageBinding.messageModel = messageModel
            receiveMessageBinding.executePendingBindings()
        }
    }


    val RECEIVED = 1
    val SEND = 2
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

    fun changeData( newItem: MutableList<MessageModel?> ){
        item.addAll(newItem)
        notifyItemRangeInserted(item.size+1,newItem.size)
    }

}