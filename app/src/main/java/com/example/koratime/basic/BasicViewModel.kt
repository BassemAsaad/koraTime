package com.example.koratime.basic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BasicViewModel<N> : ViewModel() {
    var navigator: N? = null
    val showLoading = MutableLiveData<Boolean>()
}