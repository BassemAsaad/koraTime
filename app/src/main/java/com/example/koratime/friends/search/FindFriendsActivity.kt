package com.example.koratime.friends.search

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityFindFriendsBinding


@Suppress("DEPRECATION", "SetTextI18n")
class FindFriendsActivity : BasicActivity<ActivityFindFriendsBinding, FindFriendsViewModel>(), FindFriendsNavigator {
    override val TAG: String
        get() = "SearchActivity"


    override fun getLayoutID(): Int {
        return R.layout.activity_find_friends
    }

    override fun initViewModel(): FindFriendsViewModel {
        return ViewModelProvider(this)[FindFriendsViewModel::class.java]
    }

    override fun initView() {
        callback()

    }

    override fun callback() {
        setSupportActionBar(dataBinding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "Search For People"
        }
        viewModel.apply {
            viewModel.navigator = this@FindFriendsActivity

            toastMessage.observe(this@FindFriendsActivity) {
                Toast.makeText(this@FindFriendsActivity, it, Toast.LENGTH_SHORT).show()
            }
            adapterSetup()
            adapterCallback()
        }
        dataBinding.apply {
            vm = viewModel
            recyclerView.adapter = viewModel.findFriendsAdapter

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked
        onBackPressed()
        return true
    }

}