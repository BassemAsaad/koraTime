package com.example.koratime.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemBookBinding

class TimeSlotsForManagerAdapter(
    private var timeSlots: List<String>,
    private var bookedTimesList: List<String>
) : RecyclerView.Adapter<TimeSlotsForManagerAdapter.ViewHolder>() {

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
        return timeSlots.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeSlot = timeSlots[position]
        holder.bind(timeSlot)
        if (bookedTimesList.contains(timeSlot)) {
            // Slot is already booked
            holder.dataBinding.apply {
                tvTimeSlot.isEnabled = true
                tvTimeSlot.setTextColor((Color.GRAY))
                btnBook.isEnabled = false
                btnBook.text = "Booked"
                btnBook.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            }

        } else {
            holder.dataBinding.apply {
                tvTimeSlot.isEnabled = false
                tvTimeSlot.setTextColor((Color.BLACK))
                btnBook.isEnabled = true
                btnBook.text = "Book"
                btnBook.backgroundTintList = null
            }
        }
        holder.dataBinding.tvTimeSlot.setOnLongClickListener {
            onTimeSlotClickListener?.onclick(timeSlot, holder, position)
            true
        }
        holder.dataBinding.btnBook.setOnClickListener {
            onBookClickListener?.onclick(timeSlot, holder, position)
        }
    }


    var onTimeSlotClickListener: OnTimeSlotClickListener? = null

    interface OnTimeSlotClickListener {
        fun onclick(slot: String, holder: ViewHolder, position: Int)
    }

    var onBookClickListener: OnBookClickListener? = null
    interface OnBookClickListener {
        fun onclick(slot: String, holder: ViewHolder, position: Int)
    }

    fun updateTimeSlots(newTimeSlots: List<String>, newBookedList: List<String>) {
        timeSlots = newTimeSlots
        bookedTimesList = newBookedList
        notifyDataSetChanged()
    }


}
