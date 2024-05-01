package com.example.koratime.friends

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.koratime.database.removeFriendRequestFromFirestoreWithRequestID
import com.example.koratime.databinding.FragmentFriendsBinding
import com.example.koratime.friends.search.SearchActivity
import com.example.koratime.model.FriendRequestModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class FriendsFragment : Fragment(),FriendsNavigator {
    private val usersList = mutableListOf<FriendRequestModel?>()
    lateinit var dataBinding : FragmentFriendsBinding
    private lateinit var viewModel : FriendsViewModel
    private val adapter = PendingFriendsAdapter(usersList)


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

        adapter.onAddButtonClickListener = object :PendingFriendsAdapter.OnAddButtonClickListener{
            override fun onClick(
                user: FriendRequestModel,
                holder: PendingFriendsAdapter.ViewHolder,
                position: Int
            ) {

            }
        }

    }

    override fun onStart() {
        super.onStart()

        usersList.clear()
        getFriendRequestsFromFirestore(
            DataUtils.user!!.id,
            onSuccessListener = {querySnapshot->
                for (document in querySnapshot.documents){
                    val user = document.toObject(FriendRequestModel::class.java)
                    usersList.add(user)
                    Log.e("Firebase: "," user that sent friend request, ${user?.senderID}")
                }
                adapter.changeData(usersList)

            },
            onFailureListener = {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        )
        adapter.onRemoveButtonClickListener = object :PendingFriendsAdapter.OnRemoveButtonClickListener{
            override fun onClick(
                user: FriendRequestModel,
                holder: PendingFriendsAdapter.ViewHolder,
                position: Int
            ) {
                val requestId =user.requestID
                val senderID = user.senderID
                val receiverID = Firebase.auth.currentUser!!.uid
                removeFriendRequestFromFirestoreWithRequestID(
                    sender = senderID!!,
                    receiver = receiverID,
                    request = requestId!!,
                    onSuccessListener = {
                        Log.e("Firebase: "," Request has been removed successfully $requestId")
                        adapter.removeData(user)
                    },
                    onFailureListener = {
                        Log.e("Firebase: "," Error removing request")

                    }
                )
            }
        }

    }


    override fun openSearchActivity() {
        val intent = Intent(requireContext(),SearchActivity::class.java)
        startActivity(intent)
    }





}