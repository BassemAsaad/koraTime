package com.example.koratime.notifications

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

class InApplication : Application(), LifecycleObserver {
    var isInForeground: Boolean = false
    private set
            override fun onCreate() {
        super.onCreate()
       // Lingver.init(this, Locale("en"))
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        isInForeground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        isInForeground = false
    }
}