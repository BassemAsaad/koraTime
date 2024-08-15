package com.example.koratime.location


import com.example.koratime.basic.BasicViewModel
import com.example.koratime.chat.ChatViewModel

class LocationPickerViewModel : BasicViewModel<LocationPickerNavigator>(){
    override val TAG: String
        get() = LocationPickerViewModel::class.java.simpleName
}
