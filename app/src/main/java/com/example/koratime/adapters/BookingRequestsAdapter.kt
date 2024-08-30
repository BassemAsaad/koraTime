package com.example.koratime.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemBookingRequestsBinding
import com.example.koratime.model.BookingModel

class BookingRequestsAdapter(private var requestsList: List<BookingModel?>?) :
    RecyclerView.Adapter<BookingRequestsAdapter.ViewHolder>() {


    class ViewHolder(val dataBinding: ItemBookingRequestsBinding) :
        RecyclerView.ViewHolder(dataBinding.root) {
        fun bind(friend: BookingModel) {
            dataBinding.bookModel = friend
            dataBinding.invalidateAll()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding: ItemBookingRequestsBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_booking_requests, parent, false
            )

        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return requestsList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(requestsList!![position]!!)
        holder.dataBinding.apply {
            acceptRequest.setOnClickListener {
                onItemClickListener?.onAcceptClick(position)
            }
            rejectRequest.setOnClickListener {
                onItemClickListener?.onRejectClick(position)
            }
        }


    }
    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onAcceptClick(position: Int)
        fun onRejectClick(position: Int)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun changeData(newList: List<BookingModel?>?) {
        requestsList = newList
        notifyDataSetChanged()
    }
}