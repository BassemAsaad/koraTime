package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemStadiumsBinding
import com.example.koratime.model.StadiumModel
import com.example.koratime.model.UserModel

class StadiumsAdapter (var stadiumsList : List<StadiumModel?>?): RecyclerView.Adapter<StadiumsAdapter.ViewHolder>() {

    inner class ViewHolder(val dataBinding : ItemStadiumsBinding): RecyclerView.ViewHolder(dataBinding.root){
        fun bind(stadium : StadiumModel?){
            dataBinding.stadiumModel = stadium
            dataBinding.invalidateAll()
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
    fun changeData( newStadium : List<StadiumModel?>?){
        stadiumsList = newStadium
        notifyDataSetChanged()
    }


    var onItemClickListener : OnItemClickListener?=null
    interface OnItemClickListener {
        fun onItemClick(stadium : StadiumModel?,position: Int)
    }


    private var originalUsersList: List<StadiumModel?>? = null
    fun filterUsers(query: String) {
        if (originalUsersList == null) {
            originalUsersList = stadiumsList
        }

        val filteredList = mutableListOf<StadiumModel?>()

        if (query.isEmpty()) {
            filteredList.addAll(originalUsersList ?: emptyList())
        } else {
            originalUsersList?.forEach { stadium ->
                if (stadium?.stadiumName?.contains(query, ignoreCase = true) == true) {
                    filteredList.add(stadium)
                }
            }
        }

        changeData(filteredList)
    }
}