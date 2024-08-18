package com.example.koratime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemDateBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarAdapter(private val days: List<Date>?) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {
    // Variable to store the currently selected date
    private var selectedDate: Date? = if (days!!.isNotEmpty()) days[0] else null

    inner class CalendarViewHolder(val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(date: Date) {
            binding.calendarDate.text = SimpleDateFormat("d", Locale.getDefault()).format(date)
            binding.calendarDay.text = SimpleDateFormat("EEE", Locale.getDefault()).format(date)
            // Highlight the selected date
            if (date == selectedDate) {
                binding.apply {
                    linearCalendar.setBackgroundResource(R.drawable.shape_calendar_blue)
                    calendarDate.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.white
                        )
                    )
                    calendarDay.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.white
                        )
                    )
                }
            } else {
                binding.apply {
                    linearCalendar.setBackgroundResource(R.drawable.shape_calendar)
                    calendarDate.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.black
                        )
                    )
                    calendarDay.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.dark_grey
                        )
                    )
                }
            }

            binding.invalidateAll()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding: ItemDateBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_date, parent, false
        )
        return CalendarViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return days?.size ?: 0
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days!![position]
        holder.bind(date)
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(date, holder, position)
        }


    }

    fun changeDate(date: Date) {
        selectedDate = date
        notifyDataSetChanged()
    }

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(date: Date?, holder: CalendarViewHolder, position: Int)
    }
}