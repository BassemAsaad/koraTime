package com.example.koratime.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendModel(
    var friendshipID: String?=null,
    val friendID : String?=null,
    val friendPicture : String?=null,
    val friendName : String?=null,
    var friendshipStatus: Boolean?=null
): Parcelable {
    companion object{
        const val COLLECTION_NAME = "Friend"
    }
}
