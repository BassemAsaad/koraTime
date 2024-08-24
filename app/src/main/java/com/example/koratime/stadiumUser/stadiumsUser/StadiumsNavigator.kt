package com.example.koratime.stadiumUser.stadiumsUser

import com.example.koratime.model.StadiumModel

interface StadiumsNavigator {
    fun logout()
    fun bookingStadiumActivity(stadium: StadiumModel?)
}