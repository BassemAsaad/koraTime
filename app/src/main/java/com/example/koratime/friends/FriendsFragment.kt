package com.example.koratime.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.adapters.AcceptFriendsAdapter
import com.example.koratime.adapters.AddFriendsAdapter
import com.example.koratime.adapters.UserAdapter
import com.example.koratime.database.getUsersFromFirestore
import com.example.koratime.databinding.FragmentFriendsBinding
import com.example.koratime.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class FriendsFragment : Fragment(),FriendsNavigator {
    lateinit var dataBinding : FragmentFriendsBinding
    private lateinit var viewModel : FriendsViewModel
    val adapterAccept = AcceptFriendsAdapter(null)
    val adapterAdd = AddFriendsAdapter(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_friends,container,false)
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FriendsViewModel::class.java]


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    fun initView(){
        dataBinding.vm = viewModel
        viewModel.navigator=this
        dataBinding.recyclerViewAcceptFriends.adapter = adapterAccept
        dataBinding.recyclerViewAddFriends.adapter = adapterAdd


    }

    override fun onStart() {
        super.onStart()
        val userId = Firebase.auth.currentUser?.uid
        getUsersFromFirestore(
            userId,
            onSuccessListener = {querySnapShot->
                val users = querySnapShot.toObjects(UserModel::class.java)
                adapterAdd.changeData(users)
            }
            , onFailureListener = {
                Toast.makeText(requireContext(), it.localizedMessage,  Toast.LENGTH_SHORT).show()
            }
        )
        getUsersFromFirestore(
            userId,
            onSuccessListener = {querySnapShot->
                val users = querySnapShot.toObjects(UserModel::class.java)
                adapterAccept.changeData(users)
            }
            , onFailureListener = {
                Toast.makeText(requireContext(), it.localizedMessage,  Toast.LENGTH_SHORT).show()
            }
        )


    }


}