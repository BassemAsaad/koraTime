package com.example.koratime.model

data class StadiumModel(
    var id: String?=null,
    val stadiumName: String?=null,
    val stadiumDescription: String?=null,
    val stadiumImageUrl :String?=null,
    val userManager: String? =null,
    val latitude: Double?=null,
    val longitude: Double?=null,
    val address : String?=null,
    val createdTimestamp: Long? = System.currentTimeMillis()
) {
    companion object {
        const val COLLECTION_NAME = "Stadiums"
    }
}
