package com.example.koratime.stadiums.stadiumManager

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.koratime.DataUtils
import com.example.koratime.adapters.StadiumsAdapter
import com.example.koratime.basic.BasicViewModel
import com.example.koratime.database.getUserStadiumFromFirestore
import com.example.koratime.model.StadiumModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class StadiumsManagerViewModel : BasicViewModel<StadiumsManagerNavigator>() {
    override val TAG: String
        get() = StadiumsManagerViewModel::class.java.simpleName
    val adapter = StadiumsAdapter(null)
    var toastMessage = MutableLiveData<String>()

    fun createStadium() {
        navigator?.createStadiumActivity()
    }

    fun logOut() {
        Firebase.auth.signOut()
        navigator?.logout()
    }


    fun adapterSetup() {
        getUserStadiums()
    }

    fun adapterCallback() {
        adapter.onItemClickListener = object : StadiumsAdapter.OnItemClickListener {
            override fun onItemClick(stadium: StadiumModel?, position: Int) {
                navigator?.manageStadiumActivity(stadium)
            }
        }
    }

    private fun getUserStadiums() {
        getUserStadiumFromFirestore(
            DataUtils.user!!.id,
            onSuccessListener = { querySnapShot ->
                val stadiums = querySnapShot.toObjects(StadiumModel::class.java)
                adapter.changeData(stadiums)
            },
            onFailureListener = {
                Log.e("Stadiums Adapter: ", it.localizedMessage!!.toString())
                toastMessage.value = "Error Loading Stadiums"
            }
        )
    }
}
