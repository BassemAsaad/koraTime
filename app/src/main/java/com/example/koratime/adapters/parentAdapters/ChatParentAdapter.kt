package com.example.koratime.adapters.parentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.koratime.R
import com.example.koratime.adapters.FriendsAdapter
import com.example.koratime.databinding.ItemSearchBinding
import com.example.koratime.databinding.ItemVerticalRecyclerViewBinding

class ChatParentAdapter (private var friendsAdapter: FriendsAdapter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object{
        const val VIEW_TYPE_SEARCH = 0
        const val VIEW_TYPE_FRIENDS = 1
    }
    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_SEARCH
        } else {
            VIEW_TYPE_FRIENDS
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_SEARCH -> {
                val dataBinding: ItemSearchBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.item_search, parent, false
                    )
                SearchViewHolder(dataBinding)

            }
            VIEW_TYPE_FRIENDS -> {
                val dataBinding: ItemVerticalRecyclerViewBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.item_vertical_recycler_view, parent, false
                    )
                FriendsViewHolder(dataBinding)
            }
            else-> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_SEARCH -> {
                (holder as SearchViewHolder).bind()
            }
            VIEW_TYPE_FRIENDS -> {
                (holder as FriendsViewHolder).bind(friendsAdapter)
            }

        }
    }
    inner class SearchViewHolder (val dataBinding : ItemSearchBinding) : RecyclerView.ViewHolder(dataBinding.root){
        fun bind(){
            // filter stadiums for search
            dataBinding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    // Handle query submission if needed
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    friendsAdapter.filterUsers(newText)
                    return true
                }
            })
            dataBinding.invalidateAll()
        }
    }

    inner class FriendsViewHolder (val binding : ItemVerticalRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friendsAdapter: FriendsAdapter) {
            binding.recyclerView.adapter = friendsAdapter
        }

    }

}