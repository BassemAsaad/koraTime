package com.example.koratime.stadiums.stadiumsUser

import com.example.koratime.model.StadiumModel

interface StadiumsNavigator {
    fun logout()
    fun bookingStadiumActivity(stadium: StadiumModel?)
}