package com.example.koratime.friends.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.adapters.AddFriendsAdapter
import com.example.koratime.basic.BasicActivity
import com.example.koratime.database.getUsersFromFirestore
import com.example.koratime.databinding.ActivitySearchBinding
import com.example.koratime.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.Locale

class SearchActivity : BasicActivity<ActivitySearchBinding,SearchViewModel>(),SearchNavigator {
    val adapter = AddFriendsAdapter(null)
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
        dataBinding.recyclerView.adapter = adapter

        setSupportActionBar(dataBinding.toolbar)
        // Enable back button on Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        dataBinding.searchUser.requestFocus()

        dataBinding.searchIcon.setOnClickListener {
            if (dataBinding.searchUser.text.isNullOrBlank() || dataBinding.searchUser.text.length <3){
                dataBinding.searchUser.error = "Invalid UserName"
            } else {

            }
        }
// Add a TextChangedListener to the EditText for search functionality


    }





    override fun onSupportNavigateUp(): Boolean {
        // go to the previous fragment when back button clicked on toolbar
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        val userId = Firebase.auth.currentUser?.uid
        getUsersFromFirestore(
            userId,
            onSuccessListener = {querySnapShot->
                val users = querySnapShot.toObjects(UserModel::class.java)
                adapter.changeData(users)
            }
            , onFailureListener = {
                Toast.makeText(this, it.localizedMessage,  Toast.LENGTH_SHORT).show()
            }
        )
    }
}