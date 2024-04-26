package com.example.koratime.rooms

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.adapters.RoomsAdapter
import com.example.koratime.database.getAllRoomsFromFirestore
import com.example.koratime.rooms.createRoom.AddRoomActivity
import com.example.koratime.databinding.FragmentRoomsBinding
import com.example.koratime.home.home_user.HomeActivity
import com.example.koratime.model.RoomModel

class RoomsFragment : Fragment(),RoomsNavigator {

    private lateinit var dataBinding : FragmentRoomsBinding
    private lateinit var viewModel : RoomsViewModel
    private val adapter = RoomsAdapter(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_rooms,container,false)
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[RoomsViewModel::class.java]


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }



     fun initView() {
         dataBinding.vm = viewModel
         viewModel.navigator=this

         getAllRoomsFromFirestore(
             onSuccessListener = {querySnapShot->
                 val rooms = querySnapShot.toObjects(RoomModel::class.java)
                 adapter.changeData(rooms)
             }
             , onFailureListener = {
                 Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
             }
         )


         dataBinding.recyclerView.adapter = adapter

         adapter.onItemClickListener = object : RoomsAdapter.OnItemClickListener{
             override fun onItemClick(
                 room: RoomModel?,
                 position: Int,
                 holder: RoomsAdapter.ViewHolder
             ) {
                 viewModel.roomPassword.value = room?.password
                 viewModel.password.value = holder.dataBinding.roomPasswordLayout.editText?.text.toString()

                     if (viewModel.checkRoomPassword()) {
                     (activity as? HomeActivity)?.onRoomClick(room)
                 } else {
                     holder.dataBinding.roomPasswordLayout.error = viewModel.passwordError.value
                 }
             }
         }
    }//end init


    override fun openAddRoomActivity() {
            val intent = Intent(requireContext(), AddRoomActivity::class.java)
            startActivity(intent)
    }




}