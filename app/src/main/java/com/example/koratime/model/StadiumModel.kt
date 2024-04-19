package com.example.koratime.model

data class StadiumModel(
    val id: String?=null,
    val stadiumName: String?=null,
    val stadiumDescription: String?=null,
    val stadiumImageUrl :String?=null,
    val userManager: String? =null
) {
    companion object {
        const val COLLECTION_NAME = "Stadiums"
    }
}
