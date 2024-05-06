package com.example.koratime.rooms

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.koratime.rooms.privateRooms.PrivateRoomsFragment
import com.example.koratime.rooms.publicRooms.PublicRoomsFragment

class TabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2  // Number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PublicRoomsFragment() // Your first fragment
            1 -> PrivateRoomsFragment()  // Your second fragment
            else -> throw IllegalArgumentException("Unexpected position $position")
        }
    }
}