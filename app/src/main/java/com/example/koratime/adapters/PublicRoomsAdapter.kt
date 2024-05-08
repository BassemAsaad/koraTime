package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemPublicRoomsBinding
import com.example.koratime.model.RoomModel
import com.example.koratime.model.StadiumModel

class PublicRoomsAdapter (var rooms : List<RoomModel?>?): RecyclerView.Adapter<PublicRoomsAdapter.ViewHolder>() {

    inner class ViewHolder(val dataBinding :ItemPublicRoomsBinding): RecyclerView.ViewHolder(dataBinding.root){
        fun bind(room : RoomModel?){
            dataBinding.roomModel = room
            dataBinding.invalidateAll()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val dataBinding : ItemPublicRoomsBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context)
                ,R.layout.item_public_rooms, parent,false)

        return ViewHolder(dataBinding)


    }

    override fun getItemCount(): Int {
        return rooms?.size?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = rooms!![position]
        holder.bind(room)

        holder.dataBinding.joinButton.setOnClickListener {
            onItemClickListener?.onItemClick(room,position,holder)
        }





    }
    fun changeData( newRoom : List<RoomModel?>?){
        rooms = newRoom
        notifyDataSetChanged()
    }


    var onItemClickListener : OnItemClickListener?=null
    interface OnItemClickListener{
        fun onItemClick(room : RoomModel?, position: Int,holder:ViewHolder)
    }


    private var originalUsersList: List<RoomModel?>? = null
    fun filterUsers(query: String) {
        if (originalUsersList == null) {
            originalUsersList = rooms
        }

        val filteredList = mutableListOf<RoomModel?>()

        if (query.isEmpty()) {
            filteredList.addAll(originalUsersList ?: emptyList())
        } else {
            originalUsersList?.forEach { room ->
                if (room?.name?.contains(query, ignoreCase = true) == true) {
                    filteredList.add(room)
                }
            }
        }

        changeData(filteredList)
    }
}