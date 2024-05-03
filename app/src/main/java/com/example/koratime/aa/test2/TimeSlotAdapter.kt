package com.example.koratime.aa.test2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R

class TimeSlotAdapter(private val timeSlots: List<String>) : RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTimeSlot: TextView = view.findViewById(R.id.tvTimeSlot)
        val btnBook: TextView = view.findViewById(R.id.btnBook)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_managing_stadium_times, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = timeSlots.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = timeSlots[position]
        holder.tvTimeSlot.text = slot
        holder.btnBook.setOnClickListener {
            onBookClickListener?.onclick(slot,holder,position)
        }
    }


    var onBookClickListener:OnBookClickListener?=null
    interface OnBookClickListener{
        fun onclick(slot:String,holder: ViewHolder,position: Int)
    }

}