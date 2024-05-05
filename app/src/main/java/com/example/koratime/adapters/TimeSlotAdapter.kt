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

class TimeSlotAdapter(private  var timeSlots: List<String> , private  var bookedTimesList: List<String>) : RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {
    class ViewHolder(val dataBinding: ItemBookBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun bind(timeSlot: String, bookedTimesList: List<String>) {
            dataBinding.tvTimeSlot.text = timeSlot
            dataBinding.executePendingBindings()

            Log.e("Adapter ", "List $bookedTimesList time slot $timeSlot")

            var isBooked = false
            for (bookedTime in bookedTimesList) {
                if (bookedTime == timeSlot) {
                    isBooked = true
                    break
                }
            }

            if (isBooked) {
                // Slot is already booked
                dataBinding.tvTimeSlot.isEnabled=false
                dataBinding.tvTimeSlot.setTextColor((Color.GRAY))
                dataBinding.btnBook.isEnabled=false
                dataBinding.btnBook.text= "Booked"
                dataBinding.btnBook.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            } else{
                dataBinding.tvTimeSlot.isEnabled=true
                dataBinding.tvTimeSlot.setTextColor((Color.BLACK))
                dataBinding.btnBook.isEnabled=true
                dataBinding.btnBook.text= "Book"
                dataBinding.btnBook.backgroundTintList = null
            }
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
        val bookedSlot = bookedTimesList
        holder.bind(timeSlot, bookedSlot)
        holder.dataBinding.btnBook.setOnClickListener {
            onBookClickListener?.onclick(timeSlot, holder, position)
        }

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