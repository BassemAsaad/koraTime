package com.example.koratime.basic

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BasicViewModel<N> : ViewModel() {
    var navigator: N? = null
    val showLoading = MutableLiveData<Boolean>()
    abstract val TAG: String
    protected fun log(value: Any) {
        Log.e(TAG, value.toString())
    }

}