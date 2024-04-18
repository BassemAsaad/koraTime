package com.example.koratime.friends.search

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.adapters.AddFriendsAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.getUsersFromFirestore
import com.example.koratime.databinding.ActivitySearchBinding
import com.example.koratime.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Suppress("DEPRECATION")
class SearchActivity : BasicActivity<ActivitySearchBinding,SearchViewModel>(),SearchNavigator {
    private val usersList = mutableListOf<UserModel?>()
    private val adapter = AddFriendsAdapter(usersList)
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

        val userId = Firebase.auth.currentUser?.uid
        getUsersFromFirestore(
            userId,
            onSuccessListener = { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val user = document.toObject(UserModel::class.java)
                    usersList.add(user)
                }
                adapter.changeData(usersList)
            },
            onFailureListener = {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        )



        dataBinding.recyclerView.adapter = adapter

        setSupportActionBar(dataBinding.toolbar)
        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        dataBinding.searchUser.requestFocus()
//(dataBinding.searchUser.text.isNullOrBlank() || dataBinding.searchUser.text.length <3)

        dataBinding.searchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Handle query submission if needed
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filterUsers(newText)
                return true
            }
        })



    }


    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()

    }
}