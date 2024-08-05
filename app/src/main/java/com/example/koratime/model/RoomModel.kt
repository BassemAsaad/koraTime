package com.example.koratime.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomModel(
    var roomID: String? = null,
    val name: String? = null,
    val description: String? = null,
    val password: String? = null,
    val imageUrl: String? = null,
    val userManager: String? = null,
    val playersId: List<String>? = null,
    val createdTimestamp: Long? = System.currentTimeMillis()
) : Parcelable {
    companion object {
        const val COLLECTION_NAME = "Rooms"
    }
}
