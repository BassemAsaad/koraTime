package com.example.koratime.rooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.koratime.R
import com.example.koratime.databinding.FragmentPrivateRoomsBinding
import com.example.koratime.databinding.FragmentTabsBinding
import com.google.android.material.tabs.TabLayoutMediator

class TabsFragment : Fragment() {

    private lateinit var dataBinding : FragmentTabsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_tabs,container,false)
        return dataBinding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }



    fun initView() {
        dataBinding.viewPager.adapter = TabsAdapter(this)

        // Connect the TabLayout and the ViewPager2 together
        TabLayoutMediator(dataBinding.tabs, dataBinding.viewPager) { tab, position ->

            tab.text = when (position) {
                0 -> "Public Rooms"
                1 -> "Private Rooms"
                else -> null
            }
        }.attach()

    }
}