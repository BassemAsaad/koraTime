package com.example.koratime.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.FriendsAdapter
import com.example.koratime.chat.chat_friends.ChatFriendsActivity
import com.example.koratime.database.getFriendsFromFirestore
import com.example.koratime.databinding.FragmentFriendsChatBinding
import com.example.koratime.model.FriendModel


class FriendsChatFragment : Fragment(),FriendsChatNavigator {
    lateinit var dataBinding : FragmentFriendsChatBinding
    private lateinit var viewModel : FriendsChatViewModel
    val adapter = FriendsAdapter(null)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        dataBinding.recyclerView.adapter = adapter

        adapter.onUserClickListener=object :FriendsAdapter.OnUserClickListener{
            override fun onItemClick(
                user: FriendModel?,
                holder: FriendsAdapter.ViewHolder,
                position: Int
            ) {
                val intent = Intent(requireContext(),ChatFriendsActivity::class.java)
                intent.putExtra(Constants.FRIEND,user)
                startActivity(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        getFriendsFromFirestore(
            DataUtils.user!!.id!!,
            onSuccessListener = {querySnapshot->
                val friendsList = querySnapshot.toObjects(FriendModel::class.java)
                adapter.changeData(friendsList)
            },
            onFailureListener = {e->
                Log.e("Firebase"," Error loading friends",e)
            }
        )


    }

}