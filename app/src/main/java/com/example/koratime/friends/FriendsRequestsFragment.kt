package com.example.koratime.friends

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.PendingFriendsAdapter
import com.example.koratime.basic.BasicFragment
import com.example.koratime.database.acceptFriendRequest
import com.example.koratime.database.checkIfFriendExist
import com.example.koratime.database.getFriendRequestsFromFirestore
import com.example.koratime.database.removeFriendRequestWithRequestID
import com.example.koratime.database.updateFriendshipStatus
import com.example.koratime.databinding.FragmentFriendsRequestsBinding
import com.example.koratime.friends.search.SearchActivity
import com.example.koratime.model.FriendRequestModel

class FriendsRequestsFragment :
    BasicFragment<FragmentFriendsRequestsBinding, FriendsRequestsViewModel>(),
    FriendsRequestsNavigator {


    override fun getLayoutID(): Int {
        return R.layout.fragment_friends_requests
    }

    override fun initViewModel(): FriendsRequestsViewModel {
        return ViewModelProvider(this)[FriendsRequestsViewModel::class.java]
    }

    override fun initView() {
        callback()
    }

    override fun callback() {
        viewModel.apply {
            navigator = this@FriendsRequestsFragment
            adapterSetup()
            adapterCallback()
        }
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = viewModel.adapter
        }

    }

    override fun openSearchActivity() {
        val intent = Intent(requireContext(), SearchActivity::class.java)
        startActivity(intent)
    }


}