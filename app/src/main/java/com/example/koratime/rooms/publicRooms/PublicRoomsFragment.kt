package com.example.koratime.rooms.publicRooms

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.Constants
import com.example.koratime.R
import com.example.koratime.adapters.PublicRoomsAdapter
import com.example.koratime.database.getAllRoomsFromFirestore
import com.example.koratime.databinding.FragmentPublicRoomsBinding
import com.example.koratime.model.RoomModel
import com.example.koratime.rooms.room_chat.RoomChatActivity

class PublicRoomsFragment : Fragment(), PublicRoomsNavigator {

    private lateinit var dataBinding: FragmentPublicRoomsBinding
    private lateinit var viewModel: PublicRoomsViewModel
    private val adapter = PublicRoomsAdapter(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_public_rooms, container, false)
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[PublicRoomsViewModel::class.java]


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator = this



        dataBinding.recyclerView.adapter = adapter

        adapter.onItemClickListener = object : PublicRoomsAdapter.OnItemClickListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemClick(
                room: RoomModel?,
                position: Int,
                holder: PublicRoomsAdapter.ViewHolder
            ) {
                viewModel.roomPassword.value = room?.password
                viewModel.password.value =
                    holder.dataBinding.roomPasswordLayout.editText?.text.toString()
                val intent = Intent(requireContext(), RoomChatActivity::class.java)
                intent.putExtra(Constants.ROOM, room)

                if (room!!.password != null) {
                    if (viewModel.checkRoomPassword()) {

                        startActivity(intent)
                    } else {
                        holder.dataBinding.roomPasswordLayout.error = viewModel.passwordError.value
                    }
                } else {
                    startActivity(intent)
                }

            }
        }


        // filter rooms for search
        dataBinding.searchRooms.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Handle query submission if needed
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filterUsers(newText)
                return true
            }
        })
    }//end init


    override fun onStart() {
        super.onStart()
        getAllRoomsFromFirestore(
            onSuccessListener = { querySnapShot ->
                val rooms = querySnapShot.toObjects(RoomModel::class.java)
                adapter.changeData(rooms)
            }, onFailureListener = {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }


}