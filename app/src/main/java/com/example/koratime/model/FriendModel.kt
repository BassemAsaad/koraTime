package com.example.koratime.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendModel(
    var friendshipID: String? = null,
    var requestID: String? = null,
    val friendID: String? = null,
    val friendPicture: String? = null,
    val friendName: String? = null,
    val friendShipStatus: Boolean? = null
) : Parcelable {
    companion object {
        const val SUB_COLLECTION_NAME = "Friends"
    }
}
