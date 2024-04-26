package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemRoomsBinding
import com.example.koratime.databinding.ItemStadiumsBinding
import com.example.koratime.model.RoomModel
import com.example.koratime.model.StadiumModel

class StadiumsAdapter (var stadiumsList : List<StadiumModel?>?): RecyclerView.Adapter<StadiumsAdapter.ViewHolder>() {



    inner class ViewHolder(val dataBinding : ItemStadiumsBinding): RecyclerView.ViewHolder(dataBinding.root){
        fun bind(stadium : StadiumModel?){
            dataBinding.stadiumModel = stadium
            dataBinding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding : ItemStadiumsBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context)
                ,R.layout.item_stadiums, parent,false)

        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return stadiumsList?.size?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stadium = stadiumsList!![position]
        holder.bind(stadium)
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(stadium,position)
        }
    }



    var onItemClickListener : OnItemClickListener?=null
    interface OnItemClickListener {
        fun onItemClick(stadium : StadiumModel?,position: Int)
    }

    fun changeData( newStadium : List<StadiumModel?>?){
        stadiumsList = newStadium
        notifyDataSetChanged()
    }

}