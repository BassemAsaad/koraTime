package com.example.koratime.chat

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.utils.Constants
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
            adapterCallback()
        }
        dataBinding.apply {
            vm = viewModel
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.adapterSetup()
        dataBinding.recyclerView.adapter = viewModel.chatParentAdapter

    }

    override fun openChatFriendsActivity(user: FriendModel?) {
        val intent = Intent(requireContext(), ChatFriendsActivity::class.java)
        intent.putExtra(Constants.FRIEND, user)
        startActivity(intent)
    }


}