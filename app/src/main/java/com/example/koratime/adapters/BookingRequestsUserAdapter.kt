package com.example.koratime.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.databinding.ItemBookingRequestsManagerBinding
import com.example.koratime.databinding.ItemBookingRequestsUserBinding
import com.example.koratime.model.BookingModel

class BookingRequestsUserAdapter(private var requestsList: List<BookingModel?>?) :
    RecyclerView.Adapter<BookingRequestsUserAdapter.ViewHolder>() {


    class ViewHolder(val dataBinding: ItemBookingRequestsUserBinding) :
        RecyclerView.ViewHolder(dataBinding.root) {
        fun bind(friend: BookingModel) {
            dataBinding.bookModel = friend
            dataBinding.invalidateAll()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding: ItemBookingRequestsUserBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_booking_requests_user, parent, false
            )

        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return requestsList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requestsList!![position]!!
        holder.bind(request)
        holder.dataBinding.apply {

        }


    }
    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onRejectClick(holder: ViewHolder,position: Int,stadiumId : String , userId : String, date : String, timeSlot : String)
    }
    fun changeData(newList: List<BookingModel?>?) {
        requestsList = newList
        notifyDataSetChanged()
    }
}