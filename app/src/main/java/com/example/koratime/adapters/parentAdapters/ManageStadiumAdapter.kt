package com.example.koratime.adapters.parentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.adapters.CalendarAdapter
import com.example.koratime.adapters.TimeSlotsForManagerAdapter
import com.example.koratime.databinding.ItemCalendarBinding
import com.example.koratime.databinding.ItemDateTitleBinding
import com.example.koratime.databinding.ItemTimeSlotsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManageStadiumAdapter(
    private val calendarAdapter: CalendarAdapter,
    private val timeSlotsAdapter: TimeSlotsForManagerAdapter
)  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dateTitle = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

    companion object {
        private const val VIEW_TYPE_DATE_TITLE = 0
        private const val VIEW_TYPE_CALENDAR = 1
        private const val VIEW_TYPE_TIME_SLOTS = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_DATE_TITLE
            1 -> VIEW_TYPE_CALENDAR
            2 -> VIEW_TYPE_TIME_SLOTS
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE_TITLE -> {
                val binding: ItemDateTitleBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_date_title,
                    parent,
                    false
                )
                DateTitleViewHolder(binding)
            }
            VIEW_TYPE_CALENDAR -> {
                val binding: ItemCalendarBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_calendar,
                    parent,
                    false
                )
                CalendarViewHolder(binding)
            }
            VIEW_TYPE_TIME_SLOTS -> {
                val binding: ItemTimeSlotsBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_time_slots,
                    parent,
                    false
                )
                TimeSlotsViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_DATE_TITLE -> {
                (holder as DateTitleViewHolder).bind(dateTitle)
            }
            VIEW_TYPE_CALENDAR -> {
                (holder as CalendarViewHolder).bind(calendarAdapter)
            }
            VIEW_TYPE_TIME_SLOTS -> {
                (holder as TimeSlotsViewHolder).bind(timeSlotsAdapter)
            }
        }    }

    fun changeDateTitle(date: String) {
        dateTitle = date
        notifyItemChanged(0)
    }


    // ViewHolder for Date Title
    inner class DateTitleViewHolder(private val binding: ItemDateTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dateTitle: String) {
            binding.dateTitle.text = dateTitle
        }
    }

    // ViewHolder for Calendar RecyclerView
    inner class CalendarViewHolder(private val binding: ItemCalendarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(calendarAdapter: CalendarAdapter) {
            binding.calendarRecyclerView.adapter = calendarAdapter
        }
    }

    // ViewHolder for Time Slots RecyclerView
    inner class TimeSlotsViewHolder(private val binding: ItemTimeSlotsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(timeSlotsAdapter: TimeSlotsForManagerAdapter) {
            binding.timeSlotsRecyclerView.adapter = timeSlotsAdapter
        }
    }

}