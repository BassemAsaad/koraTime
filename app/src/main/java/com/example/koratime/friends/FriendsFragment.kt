package com.example.koratime.friends

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.PendingFriendsAdapter
import com.example.koratime.database.getFriendRequestsFromFirestore
import com.example.koratime.databinding.FragmentFriendsBinding
import com.example.koratime.friends.search.SearchActivity
import com.example.koratime.model.FriendModel

class FriendsFragment : Fragment(),FriendsNavigator {
    private val usersList = mutableListOf<FriendModel?>()
    lateinit var dataBinding : FragmentFriendsBinding
    private lateinit var viewModel : FriendsViewModel
    private val adapter = PendingFriendsAdapter(null)


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
    fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator = this

        dataBinding.recyclerView.adapter = adapter

    }

    override fun onStart() {
        super.onStart()

        usersList.clear()
        getFriendRequestsFromFirestore(
            DataUtils.user!!.id,
            onSuccessListener = {querySnapshot->
                for (document in querySnapshot.documents){
                    val user = document.toObject(FriendModel::class.java)
                    usersList.add(user)
                }
                adapter.changeData(usersList)

            },
            onFailureListener = {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        )


    }












    override fun openSearchActivity() {
        val intent = Intent(requireContext(),SearchActivity::class.java)
        startActivity(intent)
    }





}