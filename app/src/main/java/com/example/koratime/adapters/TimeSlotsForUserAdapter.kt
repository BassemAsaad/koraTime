package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemBookBinding

class TimeSlotsForUserAdapter(private  var availableTimeSlots: List<String>) : RecyclerView.Adapter<TimeSlotsForUserAdapter.ViewHolder>() {
    class ViewHolder(val dataBinding: ItemBookBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun bind(timeSlot: String) {
            dataBinding.tvTimeSlot.text = timeSlot
            dataBinding.invalidateAll()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding: ItemBookBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_book, parent, false
            )
        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return availableTimeSlots.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeSlot = availableTimeSlots[position]
        holder.bind(timeSlot)
        holder.dataBinding.btnBook.setOnClickListener {
            onBookClickListener?.onclick(timeSlot, holder, position)
        }

    }

    var onBookClickListener: OnBookClickListener? = null

    interface OnBookClickListener {
        fun onclick(slot: String, holder: ViewHolder, position: Int)
    }

    fun updateTimeSlots(newTimeSlots: List<String>) {
        availableTimeSlots = newTimeSlots
        notifyDataSetChanged()
    }
}