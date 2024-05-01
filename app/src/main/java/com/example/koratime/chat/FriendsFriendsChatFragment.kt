package com.example.koratime.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.databinding.FragmentFriendsChatBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class FriendsFriendsChatFragment (): Fragment(),FriendsChatNavigator {
    lateinit var dataBinding : FragmentFriendsChatBinding
    private lateinit var viewModel : FriendsChatViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_friends_chat,container,false)
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FriendsChatViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }



    fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator=this



    }

    override fun onStart() {
        super.onStart()
        val userId = Firebase.auth.currentUser?.uid


    }

}