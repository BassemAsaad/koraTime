package com.example.koratime.model

data class FriendRequestModel(
    var requestID: String? = null,
    val userID: String? = null,
    val username: String? = null,
    val userProfilePicture: String? = null,
    val friendList: MutableList<String>? = null,
    val sender : Boolean? = null,
    val receiver : Boolean? = null,
    val status: String? = null,
    val sentAt: Long? = System.currentTimeMillis()
) {
    companion object {
        const val SUB_COLLECTION_REQUEST = "Requests"
        const val FIELD_STATUS = "status"
        const val STATUS_PENDING = "pending"
        const val FIELD_RECEIVER = "receiver"
    }
}
