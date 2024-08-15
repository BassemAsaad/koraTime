package com.example.koratime.adapters.parentAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.koratime.R
import com.example.koratime.adapters.CalendarAdapter
import com.example.koratime.adapters.TimeSlotsForManagerAdapter
import com.example.koratime.databinding.ItemCalendarBinding
import com.example.koratime.databinding.ItemDateTitleBinding
import com.example.koratime.databinding.ItemImageSliderBinding
import com.example.koratime.databinding.ItemTimeSlotsBinding
import com.example.koratime.databinding.ItemUploadImagesBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManageStadiumAdapter(
    private val calendarAdapter: CalendarAdapter,
    private val timeSlotsAdapter: TimeSlotsForManagerAdapter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dateTitle = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
    private  var images = emptyList<SlideModel>()
    private var imagePickerTextView = "Add Stadium Pictures"

    companion object {
        private const val VIEW_TYPE_IMAGE_PICKER = 0
        private const val VIEW_TYPE_IMAGE_SLIDER = 1
        private const val VIEW_TYPE_DATE_TITLE = 2
        private const val VIEW_TYPE_CALENDAR = 3
        private const val VIEW_TYPE_TIME_SLOTS = 4
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_IMAGE_PICKER
            1 -> VIEW_TYPE_IMAGE_SLIDER
            2 -> VIEW_TYPE_DATE_TITLE
            3 -> VIEW_TYPE_CALENDAR
            4 -> VIEW_TYPE_TIME_SLOTS
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_IMAGE_PICKER -> {
                val binding: ItemUploadImagesBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_upload_images,
                    parent,
                    false)
                 ImagePickerViewHolder(binding)
            }
            VIEW_TYPE_IMAGE_SLIDER->{
                val binding : ItemImageSliderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_image_slider,
                    parent,
                    false
                )
                ImageSliderViewHolder(binding)
            }
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
        return 5
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_IMAGE_PICKER -> {
                (holder as ImagePickerViewHolder).bind(imagePickerTextView)
            }
            VIEW_TYPE_IMAGE_SLIDER -> {
                (holder as ImageSliderViewHolder).bind(images)
            }
            VIEW_TYPE_DATE_TITLE -> {
                (holder as DateTitleViewHolder).bind(dateTitle)
            }

            VIEW_TYPE_CALENDAR -> {
                (holder as CalendarViewHolder).bind(calendarAdapter)
            }

            VIEW_TYPE_TIME_SLOTS -> {
                (holder as TimeSlotsViewHolder).bind(timeSlotsAdapter)
            }

        }
    }
    fun changeImageSlider(imageList: List<SlideModel>) {
        images= imageList
        notifyItemChanged(1)
    }
    fun changeDateTitle(date: String) {
        dateTitle = date
        notifyItemChanged(2)
    }
    fun changeImagePickerText(imagePickerTextView: String) {
        this.imagePickerTextView = imagePickerTextView
        notifyItemChanged(0)
    }

    inner class ImagePickerViewHolder(private val binding: ItemUploadImagesBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(imagePickerTextView: String) {
            binding.imagePicker.setOnClickListener {
               onImagePickerClickListener?.onImagePickerClick()
            }
            binding.imagePickerTextView.text = imagePickerTextView
        }
    }
    var onImagePickerClickListener: OnImagePickerClickListener? = null
    interface OnImagePickerClickListener {
        fun onImagePickerClick()
    }

    // ViewHolder for Image Slider
    inner class ImageSliderViewHolder(private val binding: ItemImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageList: List<SlideModel>) {
            when {
                imageList.isNotEmpty() -> {
                    binding.imageSlider.visibility = View.VISIBLE
                    binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
                }
                else -> binding.imageSlider.visibility = View.GONE
            }
        }
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