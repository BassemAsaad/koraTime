package com.example.koratime.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemBookBinding
import org.checkerframework.common.returnsreceiver.qual.This

@Suppress("NotifyDataSetChanged","SetTextI18n")
class TimeSlotsAdapter(
    private var timeSlots: MutableList<String>,
    private var bookedTimesList: MutableList<String>
) : RecyclerView.Adapter<TimeSlotsAdapter.ViewHolder>() {

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
        holder.dataBinding.btnBook.setOnClickListener {
            onBookClickListener?.onclick(timeSlot, holder, position)
        }
        holder.dataBinding.tvTimeSlot.setOnLongClickListener {
            onTimeSlotClickListener?.onclick(timeSlot, holder, position)
            Toast.makeText(holder.itemView.context, "$timeSlot Book Removed", Toast.LENGTH_SHORT).show()
            true
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

    fun updateTimeSlots(newTimeSlots: MutableList<String>, newBookedList: MutableList<String>) {
        timeSlots = newTimeSlots
        bookedTimesList = newBookedList
        notifyDataSetChanged()
    }


}
