package com.example.koratime.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemBookBinding

class TimeSlotAdapter(
    private var timeSlots: List<String>,
    private var availableTimeSlots: List<String>
) : RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>()
{

    class ViewHolder(val dataBinding: ItemBookBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun bind(timeSlot: String, slotState: SlotState) {
            dataBinding.tvTimeSlot.text = timeSlot
            when (slotState) {
                SlotState.AVAILABLE -> {
                    dataBinding.tvTimeSlot.isEnabled=true
                    dataBinding.tvTimeSlot.setTextColor((Color.BLACK))
                    dataBinding.btnBook.isEnabled=true
                    dataBinding.btnBook.text= "Book"
                    dataBinding.btnBook.backgroundTintList = null
                }
                SlotState.UNAVAILABLE -> {
                    dataBinding.tvTimeSlot.isEnabled=false
                    dataBinding.tvTimeSlot.setTextColor((Color.GRAY))
                    dataBinding.btnBook.isEnabled=false
                    dataBinding.btnBook.text= "Booked"
                    dataBinding.btnBook.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding: ItemBookBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_book, parent, false
        )
        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int = timeSlots.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeSlot = timeSlots[position]
        val slotState = calculateSlotState(timeSlot)
        holder.bind(timeSlot, slotState)

        holder.dataBinding.btnBook.setOnClickListener {
            onBookClickListener?.onclick(timeSlot, holder, position)
        }
    }

    var onBookClickListener: OnBookClickListener? = null

    interface OnBookClickListener {
        fun onclick(slot: String, holder: ViewHolder, position: Int)
    }

    private fun calculateSlotState(timeSlot: String): SlotState {
        if (timeSlot !in availableTimeSlots) {
            return SlotState.UNAVAILABLE
        } else {
            return SlotState.AVAILABLE
        }
    }
    fun updateTimeSlots(newTimeSlots: List<String>, newAvailableTimeSlots: List<String>) {
        timeSlots = newTimeSlots
        availableTimeSlots = newAvailableTimeSlots
        notifyDataSetChanged()
    }
    enum class SlotState {
        AVAILABLE, UNAVAILABLE
    }
}