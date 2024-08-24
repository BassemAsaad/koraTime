package com.example.koratime.stadiumUser.stadiumsUser

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.koratime.adapters.StadiumsAdapter
import com.example.koratime.adapters.parentAdapters.StadiumsParentAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.utils.getAllStadiumsFromFirestore
import com.example.koratime.model.StadiumModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class StadiumsViewModel : BasicViewModel<StadiumsNavigator>() {
    override val TAG: String
        get() = StadiumsViewModel::class.java.simpleName

    private val adapterStadiums = StadiumsAdapter(emptyList())
    lateinit var parentAdapter : StadiumsParentAdapter
    var toastMessage = MutableLiveData<String>()

    fun logOut() {
        Firebase.auth.signOut()
        navigator?.logout()
    }

    fun adapterSetup() {
        getAllStadiums()
        parentAdapter = StadiumsParentAdapter(adapterStadiums)
    }

    fun adapterCallback() {
        adapterStadiums.onItemClickListener = object : StadiumsAdapter.OnItemClickListener {
            override fun onItemClick(stadium: StadiumModel?, position: Int) {
                navigator?.bookingStadiumActivity(stadium)
            }
        }

    }

    private fun getAllStadiums() {
        getAllStadiumsFromFirestore(
            onSuccessListener = { querySnapShot ->
                val stadiums = querySnapShot.toObjects(StadiumModel::class.java)
                adapterStadiums.changeData(stadiums)
            },
            onFailureListener = {
                Log.e("Stadiums Adapter: ", it.localizedMessage!!.toString())
                toastMessage.value = "Error Loading Stadiums"
            }
        )
    }
}