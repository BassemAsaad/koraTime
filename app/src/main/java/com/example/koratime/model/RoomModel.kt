package com.example.koratime.model

import java.net.URI

data class RoomModel(
    var id: String?=null,
    val name: String?=null,
    val description: String?=null,
    val userManager: String? =null
) {
    companion object {
        const val COLLECTION_NAME = "Rooms"
    }
}
