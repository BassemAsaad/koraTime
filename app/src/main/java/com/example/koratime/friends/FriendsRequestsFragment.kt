package com.example.koratime.friends

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicFragment
import com.example.koratime.databinding.FragmentFriendsRequestsBinding
import com.example.koratime.friends.search.SearchActivity

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