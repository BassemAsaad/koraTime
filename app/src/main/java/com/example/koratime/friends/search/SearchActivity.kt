package com.example.koratime.friends.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivitySearchBinding

class SearchActivity : BasicActivity<ActivitySearchBinding,SearchViewModel>(),SearchNavigator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_search
    }

    override fun initViewModel(): SearchViewModel {
        return ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator = this

        setSupportActionBar(dataBinding.toolbar)
        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        dataBinding.searchUser.requestFocus()

        dataBinding.searchIcon.setOnClickListener {
            if (dataBinding.searchUser.text.isNullOrBlank() || dataBinding.searchUser.text.length <3){
                dataBinding.searchUser.error = "Invalid UserName"
            } else {
                searchRecyclerView(dataBinding.searchUser.text.toString())
            }
        }


    }

    private fun searchRecyclerView(search: String) {
        TODO("Not yet implemented")
    }


    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

}