package com.example.koratime.friends

import android.annotation.SuppressLint
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
import com.example.koratime.database.acceptFriendRequest
import com.example.koratime.database.getFriendRequestsFromFirestore
import com.example.koratime.database.removeFriendRequestWithRequestID
import com.example.koratime.databinding.FragmentFriendsRequestsBinding
import com.example.koratime.friends.search.SearchActivity
import com.example.koratime.model.FriendRequestModel

class FriendsRequestsFragment : Fragment(),FriendsRequestsNavigator {
    lateinit var dataBinding : FragmentFriendsRequestsBinding
    private lateinit var viewModel : FriendsRequestsViewModel
    private val adapter = PendingFriendsAdapter(null)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_friends_requests,container,false)
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FriendsRequestsViewModel::class.java]


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
            @SuppressLint("SetTextI18n")
            override fun onClick(
                user: FriendRequestModel,
                holder: PendingFriendsAdapter.ViewHolder,
                position: Int
            ) {

                acceptFriendRequest(
                    sender = user,
                    receiver = DataUtils.user!!,
                    requestID = user.requestID!!,
                    onSuccessListener = {
                        Log.e("Firebase"," ${user.senderName.toString()} Accepted Successfully ")
                        holder.dataBinding.confirmFriendButtonItem.text="Friends"
                        holder.dataBinding.confirmFriendButtonItem.isEnabled = false
                        Toast.makeText(requireContext(), "${user.senderName} Added as a friends", Toast.LENGTH_SHORT).show()

                    },
                    onFailureListener = {
                        Log.e("Firebase"," Error Accepting  ${user.senderName} ")
                    }
                )
            }
        }
        adapter.onRemoveButtonClickListener = object :PendingFriendsAdapter.OnRemoveButtonClickListener{
            override fun onClick(
                user: FriendRequestModel,
                holder: PendingFriendsAdapter.ViewHolder,
                position: Int
            ) {
                val requestId =user.requestID
                val senderID = user.senderID
                val receiverID = DataUtils.user!!.id
                removeFriendRequestWithRequestID(
                    sender = senderID!!,
                    receiver = receiverID!!,
                    request = requestId!!,
                    onSuccessListener = {
                        Log.e("Firebase: "," Request has been removed successfully $requestId")
                        Toast.makeText(requireContext(), "${user.senderName} Removed", Toast.LENGTH_SHORT).show()
                        adapter.removeData(user)
                    },
                    onFailureListener = {
                        Log.e("Firebase: "," Error removing request")

                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()

        getFriendRequestsFromFirestore(
            DataUtils.user!!.id!!,
            onSuccessListener = {querySnapshot->
                val user = querySnapshot.toObjects(FriendRequestModel::class.java)
                adapter.changeData(user)
                Log.e("Firebase: "," Friend requests loaded successfully")

            },
            onFailureListener = {e->
                Log.e("Firebase: "," Error loading friend requests: ",e)
                Toast.makeText(requireContext(), "Error Loading Friend Requests", Toast.LENGTH_SHORT).show()
            }

        )


    }


    override fun openSearchActivity() {
        val intent = Intent(requireContext(),SearchActivity::class.java)
        startActivity(intent)
    }





}