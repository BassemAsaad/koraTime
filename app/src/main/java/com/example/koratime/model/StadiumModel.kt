package com.example.koratime.model

data class StadiumModel(
    var id: String?=null,
    val stadiumName: String?=null,
    val stadiumDescription: String?=null,
    val imageUrl :String?=null,
    val userManager: String? =null
) {
    companion object {
        const val COLLECTION_NAME = "Stadiums"
    }
}
