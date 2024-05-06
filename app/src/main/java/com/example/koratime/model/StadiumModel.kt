package com.example.koratime.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StadiumModel(
    var stadiumID: String?=null,
    val stadiumName: String?=null,
    val stadiumDescription: String?=null,
    val stadiumImageUrl :String?=null,
    val userManager: String? =null,
    val opening : Int ?=null,
    val closing : Int ?=null,
    val latitude: Double?=null,
    val longitude: Double?=null,
    val address : String?=null,
    val createdTimestamp: Long? = System.currentTimeMillis()
) : Parcelable {
    companion object {
        const val COLLECTION_NAME = "Stadiums"
        const val COLLECTION_IMAGES = "StadiumImages"
    }
}
