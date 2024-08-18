package com.example.koratime.chat

import android.content.Intent
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.basic.BasicFragment
import com.example.koratime.chat.chatFriends.ChatFriendsActivity
import com.example.koratime.databinding.FragmentChatBinding
import com.example.koratime.model.FriendModel


class ChatFragment : BasicFragment<FragmentChatBinding, ChatViewModel>(), ChatNavigator {

    override fun initViewModel(): ChatViewModel {
        return ViewModelProvider(this)[ChatViewModel::class.java]
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_chat
    }

    override fun initView() {
        callback()
    }


    override fun callback() {
        viewModel.apply {
            navigator = this@ChatFragment
            adapterSetup()
            adapterCallback()
        }
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = viewModel.adapter
        }

        // filter users for search
        dataBinding.searchFriends.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Handle query submission if needed
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.adapter.filterUsers(newText)
                return true
            }
        })
    }


    override fun openChatFriendsActivity(user: FriendModel?) {
        val intent = Intent(requireContext(), ChatFriendsActivity::class.java)
        intent.putExtra(Constants.FRIEND, user)
        startActivity(intent)
    }


}