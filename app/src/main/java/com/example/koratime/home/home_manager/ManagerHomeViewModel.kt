package com.example.koratime.home.home_manager

import com.example.koratime.basic.BasicViewModel

class ManagerHomeViewModel: BasicViewModel<ManagerHomeNavigator>() {

    fun createStadium(){
        navigator?.createStadiumActivity()
    }

}