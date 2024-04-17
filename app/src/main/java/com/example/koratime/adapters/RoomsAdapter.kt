package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemRoomsBinding
import com.example.koratime.model.RoomModel

class RoomsAdapter (var rooms : List<RoomModel?>?): RecyclerView.Adapter<RoomsAdapter.ViewHolder>() {

    class ViewHolder(val dataBinding :ItemRoomsBinding): RecyclerView.ViewHolder(dataBinding.root){
        fun bind(room : RoomModel?){
            dataBinding.vm = room
            dataBinding.invalidateAll()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val dataBinding : ItemRoomsBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context)
                ,R.layout.item_rooms, parent,false)

        return ViewHolder(dataBinding)


    }

    override fun getItemCount(): Int {
        return rooms?.size?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rooms!![position])

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(rooms!![position],position)
        }
    }
    fun changeData( newRoom : List<RoomModel?>?){
        rooms = newRoom
        notifyDataSetChanged()
    }


    var onItemClickListener : OnItemClickListener?=null
    interface OnItemClickListener{
        fun onItemClick(room : RoomModel?, position: Int)
    }
}