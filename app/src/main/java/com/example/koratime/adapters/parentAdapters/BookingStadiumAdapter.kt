package com.example.koratime.adapters.parentAdapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.koratime.utils.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.CalendarAdapter
import com.example.koratime.adapters.TimeSlotsAdapter
import com.example.koratime.utils.playerDocumentExists
import com.example.koratime.databinding.ItemDateTitleBinding
import com.example.koratime.databinding.ItemHorizontalRecyclerViewBinding
import com.example.koratime.databinding.ItemImageSliderBinding
import com.example.koratime.databinding.ItemPlayersSearchBinding
import com.example.koratime.databinding.ItemVerticalRecyclerViewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingStadiumAdapter(
    private val calendarAdapter: CalendarAdapter,
    private val timeSlotsAdapter: TimeSlotsAdapter,
    private val stadiumId: String
) : RecyclerView.Adapter<ViewHolder>() {
    private var dateTitle = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
    private var images = emptyList<SlideModel>()

    companion object {
        private const val VIEW_TYPE_IMAGE_SLIDER = 0
        private const val VIEW_TYPE_PLAYERS_SEARCH = 1
        private const val VIEW_TYPE_DATE_TITLE = 2
        private const val VIEW_TYPE_CALENDAR = 3
        private const val VIEW_TYPE_TIME_SLOTS = 4
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_IMAGE_SLIDER
            1 -> VIEW_TYPE_PLAYERS_SEARCH
            2 -> VIEW_TYPE_DATE_TITLE
            3 -> VIEW_TYPE_CALENDAR
            4 -> VIEW_TYPE_TIME_SLOTS
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_IMAGE_SLIDER -> {
                val binding: ItemImageSliderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_image_slider,
                    parent,
                    false
                )
                ImageSliderViewHolder(binding)
            }

            VIEW_TYPE_PLAYERS_SEARCH -> {
                val binding: ItemPlayersSearchBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_players_search,
                    parent,
                    false
                )
                PlayersSearchViewHolder(binding)
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
                val binding: ItemHorizontalRecyclerViewBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_horizontal_recycler_view,
                    parent,
                    false
                )
                CalendarViewHolder(binding)
            }

            VIEW_TYPE_TIME_SLOTS -> {
                val binding: ItemVerticalRecyclerViewBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_vertical_recycler_view,
                    parent,
                    false
                )
                TimeSlotsViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")

        }
    }

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_IMAGE_SLIDER -> {
                (holder as ImageSliderViewHolder).bind(images)
            }

            VIEW_TYPE_PLAYERS_SEARCH -> {
                (holder as PlayersSearchViewHolder).bind(stadiumId)
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
        images = imageList
        notifyItemChanged(0)
    }

    fun changeDateTitle(date: String) {
        dateTitle = date
        notifyItemChanged(3)
    }

    inner class PlayersSearchViewHolder(val dataBinding: ItemPlayersSearchBinding) :
        ViewHolder(dataBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(stadiumId: String) {
            playerDocumentExists(
                stadiumID = stadiumId,
                userID = DataUtils.user!!.id!!,
                onSuccessListener = { playerExist ->
                    Log.e("BookingStadiumAdapter", "Player Exist ? $playerExist")
                    if (playerExist) {
                        dataBinding.lookForPlayers.text = "Looking For Players..."
                        dataBinding.lookForPlayers.isEnabled = false
                        dataBinding.stopSearching.visibility = View.VISIBLE
                    } else {
                        dataBinding.lookForPlayers.text = "Click To Search For Players"
                        dataBinding.lookForPlayers.isEnabled = true
                        dataBinding.stopSearching.visibility = View.GONE
                    }
                    Log.e("BookingStadiumAdapter", "Player Exist ? $playerExist")
                },
                onFailureListener = { e ->
                    Log.e("BookingStadiumAdapter", "Error Finding The player: ", e)

                }
            )
            dataBinding.lookForPlayers.setOnClickListener {
                onSearchClickListener?.onSearchClick(this, stadiumId)
            }
            dataBinding.stopSearching.setOnClickListener {
                onSearchClickListener?.onStopSearchClick(this, stadiumId)
            }

        }
    }

    var onSearchClickListener: OnSearchClickListener? = null

    interface OnSearchClickListener {
        fun onSearchClick(holder: PlayersSearchViewHolder, stadiumId: String)
        fun onStopSearchClick(holder: PlayersSearchViewHolder, stadiumId: String)
    }

    // ViewHolder for Image Slider
    inner class ImageSliderViewHolder(private val binding: ItemImageSliderBinding) :
        ViewHolder(binding.root) {
        fun bind(imageList: List<SlideModel>) {
            when {
                imageList.isNotEmpty() -> {
                    binding.cardView.visibility = View.VISIBLE
                    binding.imageSlider.visibility = View.VISIBLE
                    binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
                }

                else -> {
                    binding.imageSlider.visibility = View.GONE
                    binding.cardView.visibility = View.GONE
                }
            }
        }
    }

    // ViewHolder for Date Title
    inner class DateTitleViewHolder(private val binding: ItemDateTitleBinding) :
        ViewHolder(binding.root) {
        fun bind(dateTitle: String) {
            binding.dateTitle.text = dateTitle
        }
    }

    // ViewHolder for Calendar RecyclerView
    inner class CalendarViewHolder(private val binding: ItemHorizontalRecyclerViewBinding) :
        ViewHolder(binding.root) {
        fun bind(calendarAdapter: CalendarAdapter) {
            binding.recyclerView.adapter = calendarAdapter
        }
    }

    // ViewHolder for Time Slots RecyclerView
    inner class TimeSlotsViewHolder(private val binding: ItemVerticalRecyclerViewBinding) :
        ViewHolder(binding.root) {
        fun bind(timeSlotsAdapter: TimeSlotsAdapter) {
            binding.recyclerView.adapter = timeSlotsAdapter
        }
    }
}