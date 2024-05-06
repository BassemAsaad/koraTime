package com.example.koratime.rooms.privateRooms

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
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.RoomsAdapter
import com.example.koratime.database.getAllRoomsFromFirestore
import com.example.koratime.database.getUserRoomsFromFirestore
import com.example.koratime.databinding.FragmentPrivateRoomsBinding
import com.example.koratime.databinding.FragmentPublicRoomsBinding
import com.example.koratime.rooms.createRoom.AddRoomActivity
import com.example.koratime.model.RoomModel
import com.example.koratime.rooms.room_chat.RoomChatActivity

class PrivateRoomsFragment : Fragment(), PrivateRoomsNavigator {

    private lateinit var dataBinding : FragmentPrivateRoomsBinding
    private lateinit var viewModel : PrivateRoomsViewModel
    private val adapter = RoomsAdapter(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_private_rooms,container,false)
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[PrivateRoomsViewModel::class.java]


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }



     fun initView() {
         dataBinding.vm = viewModel
         viewModel.navigator=this

         dataBinding.recyclerView.adapter = adapter

         adapter.onItemClickListener = object : RoomsAdapter.OnItemClickListener{
             @SuppressLint("SuspiciousIndentation")
             override fun onItemClick(
                 room: RoomModel?,
                 position: Int,
                 holder: RoomsAdapter.ViewHolder
             ) {
                 viewModel.roomPassword.value = room?.password
                 viewModel.password.value = holder.dataBinding.roomPasswordLayout.editText?.text.toString()
                 val intent = Intent(requireContext(),RoomChatActivity::class.java)
                 intent.putExtra(Constants.ROOM,room)

                 if (room!!.password!=null){
                     if (viewModel.checkRoomPassword() ) {

                         startActivity(intent)
                     } else {
                         holder.dataBinding.roomPasswordLayout.error = viewModel.passwordError.value
                     }
                 }else{
                     startActivity(intent)
                 }

             }
         }
    }//end init


    override fun openAddRoomActivity() {
            val intent = Intent(requireContext(), AddRoomActivity::class.java)
            startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        getUserRoomsFromFirestore(
            userId = DataUtils.user!!.id!!,
            onSuccessListener = {querySnapShot->
                val rooms = querySnapShot.toObjects(RoomModel::class.java)
                adapter.changeData(rooms)
                Log.e("Firebase"," Private rooms loaded successfully ")

            }
            , onFailureListener = {e->
                Log.e("Firebase"," Private rooms error: ",e)
            }
        )
    }



}