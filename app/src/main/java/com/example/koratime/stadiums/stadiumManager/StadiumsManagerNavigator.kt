package com.example.koratime.stadiums.stadiumManager

import com.example.koratime.model.StadiumModel

interface StadiumsManagerNavigator {
    fun logout()
    fun createStadiumActivity()
    fun manageStadiumActivity(stadium: StadiumModel?)
}