package com.example.koratime.rooms

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.koratime.rooms.privateRooms.PrivateRoomsFragment
import com.example.koratime.rooms.publicRooms.PublicRoomsFragment
import com.example.koratime.rooms.stadiumRooms.StadiumRoomsFragment

class TabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3  // Number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PublicRoomsFragment()
            1 -> PrivateRoomsFragment()
            2 -> StadiumRoomsFragment()
            else -> throw IllegalArgumentException("Unexpected position $position")
        }
    }
}