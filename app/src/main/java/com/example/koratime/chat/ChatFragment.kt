package com.example.koratime.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.adapters.UserAdapter
import com.example.koratime.database.getRoomsFromFirestore
import com.example.koratime.database.getUsersFromFirestore
import com.example.koratime.databinding.FragmentChatBinding
import com.example.koratime.friends_messages.FriendsChatActivity
import com.example.koratime.home.home_user.HomeActivity
import com.example.koratime.model.RoomModel
import com.example.koratime.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.QuerySnapshot

class ChatFragment (room: RoomModel?,): Fragment(),ChatNavigator {
    lateinit var dataBinding : FragmentChatBinding
    private lateinit var viewModel : ChatViewModel
    val adapter = UserAdapter(null)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_chat,container,false)
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =  ViewModelProvider(this).get(ChatViewModel::class.java)

        adapter.onUserClickListener = object : UserAdapter.OnUserClickListener{
            override fun onItemClick(user: UserModel?, position: Int) {
                val intent = Intent(requireContext(),FriendsChatActivity::class.java)
                intent.putExtra(Constants.USER_NAME,user?.userName)
                startActivity(intent)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }



    fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator=this
        dataBinding.recyclerView.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        val userId = Firebase.auth.currentUser?.uid
        getUsersFromFirestore(
            userId,
            onSuccessListener = {querySnapShot->
                val users = querySnapShot.toObjects(UserModel::class.java)
                adapter.changeData(users)
            }
            , onFailureListener = {
                Toast.makeText(requireContext(), it.localizedMessage,  Toast.LENGTH_SHORT).show()
            }
        )
    }

}