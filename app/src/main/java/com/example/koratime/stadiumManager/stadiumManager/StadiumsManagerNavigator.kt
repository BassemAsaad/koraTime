package com.example.koratime.stadiumManager.stadiumManager

import com.example.koratime.model.StadiumModel

interface StadiumsManagerNavigator {
    fun logout()
    fun createStadiumActivity()
    fun manageStadiumActivity(stadium: StadiumModel?)
}