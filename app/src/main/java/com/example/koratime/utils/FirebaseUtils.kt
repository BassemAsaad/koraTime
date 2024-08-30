package com.example.koratime.utils

import android.net.Uri
import android.util.Log
import com.example.koratime.model.BookingModel
import com.example.koratime.model.FriendMessageModel
import com.example.koratime.model.FriendModel
import com.example.koratime.model.FriendRequestModel
import com.example.koratime.model.RoomMessageModel
import com.example.koratime.model.RoomModel
import com.example.koratime.model.StadiumModel
import com.example.koratime.model.UserModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Date
import java.util.UUID

fun addUserToFirestore(
    user: UserModel,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val collection = db.collection(UserModel.COLLECTION_NAME)
    collection
        .document(user.id!!)
        .set(user)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getUserFromFirestore(
    uid: String?,
    onSuccessListener: OnSuccessListener<DocumentSnapshot>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val collection = db.collection(UserModel.COLLECTION_NAME)
    collection
        .document(uid!!)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getUsersFromFirestore(
    currentUserId: String?
) : Query {
    val db = Firebase.firestore
    val collection = db.collection(UserModel.COLLECTION_NAME)
   return collection
        .whereNotEqualTo("id", currentUserId)

}

fun updateUserLocationInFirestore(
    userId: String,
    latitude: Double,
    longitude: Double,
    cityName: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val userRef = db.collection(UserModel.COLLECTION_NAME).document(userId)
    userRef.update(
        mapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "city" to cityName
        )
    )
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun addRoomToFirestore(
    room: RoomModel,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
    val document = collection.document()
    room.roomID = document.id
    document.set(room)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun removeRoomFromFirestore(
    roomId: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
    val document = collection.document(roomId)
    document.delete()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getAllRoomsFromFirestore() : Query  {
    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
    return collection
        .whereEqualTo("playersId", null)
        .orderBy("createdTimestamp", Query.Direction.DESCENDING)

}

fun getUserRoomsFromFirestore(
    userId: String,
) : Query {
    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
    return collection
        .whereEqualTo("userManager", userId)
        .orderBy("createdTimestamp", Query.Direction.DESCENDING)

}

fun addStadiumRoomToFirestore(
    stadium: StadiumModel,
    playerIds: List<String>,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {

    val room = RoomModel(
        name = stadium.stadiumName,
        description = "10 players are added to this room only the 10 players can see the room",
        imageUrl = stadium.stadiumImageUrl,
        playersId = playerIds,
        userManager = stadium.userManager
    )
    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
    val document = collection.document()
    room.roomID = document.id
    document.set(room)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getStadiumRoomFromFirestore(
    playerID: String,
    onSuccessListener: OnSuccessListener<QuerySnapshot>,
    onFailureListener: OnFailureListener
) {

    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
    val query = collection.whereArrayContains("playersId", playerID)
    query.get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}


fun addRoomMessageToFirestore(
    message: RoomMessageModel,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {

    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
        .document(message.roomID!!)
        .collection(RoomMessageModel.COLLECTION_NAME)
    val messageRef = collection.document()
    message.messageID = messageRef.id
    messageRef.set(message)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getRoomMessagesFromFirestore(roomId: String): Query {
    val db = Firebase.firestore
    val roomRef = db.collection(RoomModel.COLLECTION_NAME)
        .document(roomId)

    return roomRef.collection(RoomMessageModel.COLLECTION_NAME)
        .orderBy("dateTime", Query.Direction.ASCENDING)
}

fun uploadImageToStorage(
    imageUri: Uri?,
    onSuccessListener: OnSuccessListener<Uri>,
    onFailureListener: OnFailureListener
) {
    if (imageUri != null) {
        // upload the user selected image
        val storage = Firebase.storage
        val storageRef = storage.reference.child("images/${UUID.randomUUID()}")
        val uploadImage = storageRef.putFile(imageUri)

        uploadImage
            .addOnSuccessListener {
                storageRef.downloadUrl
                    .addOnSuccessListener { uri -> onSuccessListener.onSuccess(uri) }
                    .addOnFailureListener(onFailureListener)
            }
            .addOnFailureListener(onFailureListener)
    }
}

fun uploadMultipleImagesToStorage(
    uris: List<Uri>,
    stadiumID: String,
    onSuccessListener: OnSuccessListener<List<String>>,
    onFailureListener: OnFailureListener
) {
    val imageUrls = mutableListOf<String>()
    val storageRef = Firebase.storage.reference
    val imagesRef = storageRef.child("stadiums/$stadiumID")

    for ((index, uri) in uris.withIndex()) {
        val imageRef = imagesRef.child("image$index")
        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                // Get a URL to the uploaded content
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    imageUrls.add(imageUrl)
                }.addOnSuccessListener {
                    onSuccessListener.onSuccess(imageUrls)
                }

            }
            .addOnFailureListener {
                // Handle unsuccessful uploads
                Log.e("ImagePicker", "Image upload failed: ${it.message}")
                onFailureListener.onFailure(it)
            }
    }

}

fun addMultipleImagesToFirestore(
    imageUris: List<String>,
    stadiumID: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val stadiumImagesRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_IMAGES).document("ImageLinks")
    val data = hashMapOf<String, String>()
    imageUris.forEachIndexed { index, imageUri ->
        data["image${index}"] = imageUri
    }
    stadiumImagesRef.set(data)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getMultipleImagesFromFirestore(
    stadiumID: String,
    onSuccessListener: OnSuccessListener<List<String>>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val stadiumImagesRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_IMAGES).document("ImageLinks")
    stadiumImagesRef.get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val imageUris = mutableListOf<String>()
                for (i in 0 until documentSnapshot.data!!.size) {
                    val imageUri = documentSnapshot.data!!["image$i"] as String
                    imageUris.add(imageUri)
                }
                onSuccessListener.onSuccess(imageUris)
            } else {
                onFailureListener.onFailure(Exception("Document does not exist"))
            }
        }
        .addOnFailureListener { exception ->
            onFailureListener.onFailure(exception)
        }
}

fun addFriendRequestToFirestore(
    currentUser: UserModel,
    receiver: UserModel,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {

    val friendShip = mutableListOf<String>()
    friendShip.add(currentUser.id!!)
    friendShip.add(receiver.id!!)

    val requestSender = FriendRequestModel(
        userID = currentUser.id,
        username = currentUser.userName,
        userProfilePicture = currentUser.profilePicture,
        status = "pending",
        friendList = friendShip,
        receiver = true,
        sender = false

    )
    val requestReceiver = FriendRequestModel(
        userID = receiver.id,
        username = receiver.userName,
        userProfilePicture = receiver.profilePicture,
        status = "pending",
        friendList = friendShip,
        receiver = false,
        sender = true
    )

    val db = Firebase.firestore
    // create a unique ID for the friend request
    val requestId = db.collection(UserModel.COLLECTION_NAME)
        .document().id
    // Add the friend request to receiverUser
    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
        .document(receiver.id)
        .collection(FriendRequestModel.SUB_COLLECTION_REQUEST)
        .document(requestId)

    // Add the friend request to senderUser
    val senderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(currentUser.id)
        .collection(FriendRequestModel.SUB_COLLECTION_REQUEST)
        .document(requestId)
    // set id
    requestSender.requestID = requestId
    requestReceiver.requestID = requestId

    // batch write can improve performance and reduce the risk of data inconsistency.
    val batch = db.batch()
    batch.set(senderRef, requestReceiver)
    batch.set(receiverRef, requestSender)

    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}


fun checkFriendRequestStatusFromFirestore(
    currentUser: String,
    receiver: String,
    onSuccessListener: OnSuccessListener<String>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
    receiverRef
        .document(currentUser)
        .collection(FriendRequestModel.SUB_COLLECTION_REQUEST)
        .whereArrayContains("friendList", receiver)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Iterate over the documents in the query snapshot
                querySnapshot.forEach { document ->
                    // Get the status of the friend request
                    val status = document.getString("status")
                    onSuccessListener.onSuccess(status)
                }
            } else {
                onSuccessListener.onSuccess("not_found")

            }
        }
        .addOnFailureListener(onFailureListener)

}

fun getFriendRequestsFromFirestore(
    user: UserModel,
    onSuccessListener: OnSuccessListener<QuerySnapshot>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val collectionRef = db.collection(UserModel.COLLECTION_NAME)
    val receiverRef = collectionRef.document(user.id!!)
    receiverRef.collection(FriendRequestModel.SUB_COLLECTION_REQUEST)
        .whereEqualTo(FriendRequestModel.FIELD_STATUS, FriendRequestModel.STATUS_PENDING)
        .whereEqualTo(FriendRequestModel.FIELD_RECEIVER, true)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun acceptFriendRequest(
    sender: FriendRequestModel,
    receiver: UserModel,
    requestID: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {

    val friendSender = FriendModel(
        friendID = receiver.id,
        friendPicture = receiver.profilePicture,
        friendName = receiver.userName,
        friendShipStatus = true,
        requestID = sender.requestID!!
    )
    val friendReceiver = FriendModel(
        friendID = sender.userID,
        friendPicture = sender.userProfilePicture,
        friendName = sender.username,
        friendShipStatus = true,
        requestID = sender.requestID!!
    )
    val db = Firebase.firestore

    val requestId = db.collection(UserModel.COLLECTION_NAME)
        .document().id
    // create friend collection for receiver
    val friendReceiverRef = db.collection(UserModel.COLLECTION_NAME)
        .document(receiver.id!!)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(requestId)

    // create friend collection for sender
    val friendSenderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(sender.userID!!)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(requestId)

    // set id
    friendSender.friendshipID = friendSenderRef.id
    friendReceiver.friendshipID = friendReceiverRef.id


    // batch write can improve performance and reduce the risk of data inconsistency.
    val batch = db.batch()
    batch.set(friendReceiverRef, friendReceiver)
    batch.set(friendSenderRef, friendSender)

    batch.commit()
        .addOnSuccessListener {
            updateFriendRequestStatusToAccepted(
                sender = sender.userID,
                currentUser = receiver.id,
                requestId = requestID,
                onSuccessListener = onSuccessListener,
                onFailureListener = onFailureListener
            )
        }
        .addOnFailureListener(onFailureListener)
}

fun checkIfFriendExist(
    currentUser: UserModel,
    userSender: FriendRequestModel,
    onSuccessListener: OnSuccessListener<Boolean>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val collectionRef = db.collection(UserModel.COLLECTION_NAME)
        .document(currentUser.id!!)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .whereEqualTo("friendID", userSender.userID)
    collectionRef.get()
        .addOnSuccessListener { document ->
            if (!document.isEmpty) {
                // Document exists
                onSuccessListener.onSuccess(true)
            } else {
                // Document does not exist
                onSuccessListener.onSuccess(false)
            }
        }
        .addOnFailureListener(onFailureListener)
}

fun updateFriendshipStatus(
    currentUser: UserModel,
    sender: FriendRequestModel,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val updates = mapOf(
        "friendShipStatus" to true,
        "requestID" to sender.requestID
    )
    val firstUser = db.collection(UserModel.COLLECTION_NAME)
        .document(currentUser.id!!)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .whereEqualTo("friendID", sender.userID)
        .get()

    val secondUser = db.collection(UserModel.COLLECTION_NAME)
        .document(sender.userID!!)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .whereEqualTo("friendID", currentUser.id)
        .get()

    secondUser.addOnSuccessListener { documents ->
        if (!documents.isEmpty) {
            documents.documents[0].reference.update(updates)
        } else {
            onFailureListener.onFailure(java.lang.Exception("Document second user not found "))
        }
    }
        .addOnFailureListener(onFailureListener)


    firstUser
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                documents.forEach { document ->
                    document.reference.update(updates)
                        .addOnSuccessListener {
                            updateFriendRequestStatusToAccepted(
                                sender = sender.userID,
                                currentUser = currentUser.id,
                                requestId = sender.requestID!!,
                                onSuccessListener = onSuccessListener,
                                onFailureListener = onFailureListener
                            )
                        }
                        .addOnFailureListener(onFailureListener)
                }
            } else {
                onFailureListener.onFailure(java.lang.Exception("Document first user not found "))

            }
        }
        .addOnFailureListener(onFailureListener)
}

fun updateFriendRequestStatusToAccepted(
    sender: String,
    currentUser: String,
    requestId: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {

    val db = Firebase.firestore
    // create a unique ID for the friend request
    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
        .document(currentUser)
        .collection(FriendRequestModel.SUB_COLLECTION_REQUEST)
        .document(requestId)
    // Add the friend request to senderUser
    val senderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(sender)
        .collection(FriendRequestModel.SUB_COLLECTION_REQUEST)
        .document(requestId)

    // batch write can improve performance and reduce the risk of data inconsistency.
    val batch = db.batch()
    batch.update(senderRef, "status", "accepted")
    batch.update(receiverRef, "status", "accepted")

    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getFriendsFromFirestore(
    currentUser: String,
    onSuccessListener: OnSuccessListener<QuerySnapshot>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val friendRef = db.collection(UserModel.COLLECTION_NAME)
    friendRef
        .document(currentUser)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .whereEqualTo("friendShipStatus", true)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)

}

fun removeFriendRequestWithoutRequestID(
    sender: String,
    receiver: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    // Create a query to find the friend request document in the receiver's collection of pending friend requests
    val receiverQuery = db.collection(UserModel.COLLECTION_NAME)
        .document(receiver)
        .collection(FriendRequestModel.SUB_COLLECTION_REQUEST)
        .whereEqualTo("receiverID", receiver)

    // Create a query to find the friend request document in the sender's collection of sent friend requests
    val senderQuery = db.collection(UserModel.COLLECTION_NAME)
        .document(sender)
        .collection(FriendRequestModel.SUB_COLLECTION_REQUEST)
        .whereEqualTo("senderID", sender)

    // Delete the friend request documents
    receiverQuery.get()
        .addOnSuccessListener { receiverDocuments ->
            for (receiverDocument in receiverDocuments) {
                receiverDocument.reference.delete()
            }
            senderQuery.get()
                .addOnSuccessListener { senderDocuments ->
                    for (senderDocument in senderDocuments) {
                        senderDocument.reference.delete()
                    }
                    onSuccessListener.onSuccess(null)
                }
                .addOnFailureListener(onFailureListener)
        }
        .addOnFailureListener(onFailureListener)

}

fun removeFriendRequestWithRequestID(
    sender: String,
    receiver: String,
    request: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {

    val db = Firebase.firestore

    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
        .document(receiver)
        .collection(FriendRequestModel.SUB_COLLECTION_REQUEST)
        .document(request)

    val senderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(sender)
        .collection(FriendRequestModel.SUB_COLLECTION_REQUEST)
        .document(request)


    // batch write can improve performance and reduce the risk of data inconsistency.
    val batch = db.batch()
    batch.delete(senderRef)
    batch.delete(receiverRef)

    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun removeFriendFromFirestore(
    user1: UserModel,
    user2: FriendModel,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore

    // Get a reference to the friendship document for the sender
    val firstUser = db.collection(UserModel.COLLECTION_NAME)
        .document(user1.id!!)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(user2.friendshipID!!)

    // Get a reference to the friendship document for the receiver
    val secondUser = db.collection(UserModel.COLLECTION_NAME)
        .document(user2.friendID!!)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(user2.friendshipID!!)

    // Batch write can improve performance and reduce the risk of data inconsistency.
    val batch = db.batch()
    batch.update(firstUser, "friendShipStatus", false)
    batch.update(secondUser, "friendShipStatus", false)


    batch.commit()
        .addOnSuccessListener {
            removeFriendRequestWithRequestID(
                sender = user1.id,
                receiver = user2.friendID,
                request = user2.requestID!!,
                onSuccessListener = onSuccessListener,
                onFailureListener = onFailureListener
            )
        }
        .addOnFailureListener(onFailureListener)
}

fun addFriendMessageToFirestore(
    message: FriendMessageModel,
    friendshipID: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {

    val db = Firebase.firestore

    // create a uniqueID for message
    val messageID = db.collection(UserModel.COLLECTION_NAME)
        .document().id

    val senderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(message.senderID!!)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(friendshipID)
        .collection(FriendMessageModel.COLLECTION_NAME)
        .document(messageID)

    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
        .document(message.receiverID!!)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(friendshipID)
        .collection(FriendMessageModel.COLLECTION_NAME)
        .document(messageID)

    message.messageID = messageID

    val batch = db.batch()
    batch.set(senderRef, message)
    batch.set(receiverRef, message)
    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)

}

fun getFriendMessagesFromFirestore(
    senderID: String,
    friendshipID: String
): Query {
    val db = Firebase.firestore

    val senderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(senderID)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(friendshipID)


    return senderRef.collection(FriendMessageModel.COLLECTION_NAME)
        .orderBy("dateTime", Query.Direction.ASCENDING)
}

fun getLastMessageFromFirestore(
    userID: String,
    friendshipID: String,
    onSuccessListener: OnSuccessListener<QuerySnapshot>,
    onFailureListener: OnFailureListener
) {

    val db = Firebase.firestore
    val query = db.collection(UserModel.COLLECTION_NAME)
        .document(userID)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(friendshipID)
        .collection(FriendMessageModel.COLLECTION_NAME)
        .orderBy("dateTime", Query.Direction.DESCENDING)
        .limit(1)
    query.get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)

}

fun addStadiumToFirestore(
    stadium: StadiumModel,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val collection = db.collection(StadiumModel.COLLECTION_NAME)
    val document = collection.document()
    stadium.stadiumID = document.id
    document.set(stadium)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getAllStadiumsFromFirestore(
    onSuccessListener: OnSuccessListener<QuerySnapshot>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val collection = db.collection(StadiumModel.COLLECTION_NAME)
    collection
        .orderBy("createdTimestamp", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getUserStadiumFromFirestore(
    userId: String?,
    onSuccessListener: OnSuccessListener<QuerySnapshot>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val collection = db.collection(StadiumModel.COLLECTION_NAME)
    collection
        .whereEqualTo("userManager", userId)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun deleteStadiumFromFirestore(
    stadiumID: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val collection = db.collection(StadiumModel.COLLECTION_NAME)
    val document = collection.document(stadiumID)
    document.delete()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun addBookingToFirestore(
    timeSlot: String,
    stadium: StadiumModel,
    date: String,
    user: UserModel,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val bookingData = BookingModel(
        userId = user.id,
        date = date,
        timeSlot = timeSlot,
        status = "pending",
        dateTime = Date().time,
        userName = user.userName,
        stadiumName = stadium.stadiumName,
        stadiumID = stadium.stadiumID,
        userPicture = user.profilePicture,
        stadiumPicture = stadium.stadiumImageUrl

    )
    val db = Firebase.firestore
    val setDate = mapOf(
        "date" to date
    )
    val bookingRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadium.stadiumID!!)
        .collection(BookingModel.COLLECTION_NAME).document(date)
    bookingRef.set(setDate)

    val managerRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadium.stadiumID!!)
        .collection(BookingModel.COLLECTION_NAME).document(date)
        .collection(BookingModel.SUB_COLLECTION_NAME).document(timeSlot)
    managerRef.set(bookingData)

    val userRef = db.collection(UserModel.COLLECTION_NAME).document(user.id!!)
        .collection(BookingModel.COLLECTION_NAME).document(date)
    userRef.set(bookingData)
    val batch = db.batch()
    batch.set(bookingRef, setDate)
    batch.set(managerRef, bookingData)
    batch.set(userRef, bookingData)
    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getBookingRequestsFromFirestore(
    stadiumID: String,
    onSuccessListener: OnSuccessListener<List<BookingModel>>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val datesRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(BookingModel.COLLECTION_NAME)

    datesRef.get()
        .addOnSuccessListener { querySnapshot ->
            val tasks = mutableListOf<Task<QuerySnapshot>>()
            for (document in querySnapshot.documents) {
                val ref = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
                    .collection(BookingModel.COLLECTION_NAME).document(document.id)
                    .collection(BookingModel.SUB_COLLECTION_NAME)
                    .whereEqualTo(BookingModel.FIELD_STATUS, BookingModel.STATUS_PENDING)
                    .orderBy(BookingModel.FIELD_DATE_TIME, Query.Direction.ASCENDING)
                    .get()

                tasks.add(ref) // Collecting tasks
            }

            // Wait for all tasks to complete
            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener { taskResults ->
                    val list = mutableListOf<BookingModel>()
                    for (task in taskResults) {
                        if (task.isSuccessful) {
                            val query = task.result as QuerySnapshot
                            list.addAll(query.toObjects(BookingModel::class.java))
                        }
                    }
                    onSuccessListener.onSuccess(list)
                }
                .addOnFailureListener(onFailureListener)
        }
        .addOnFailureListener(onFailureListener)
}

fun acceptBookingRequest(
    stadiumID: String,
    userID: String,
    date: String,
    timeSlot: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore

    val bookingRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(BookingModel.COLLECTION_NAME).document(date)
        .collection(BookingModel.SUB_COLLECTION_NAME).document(timeSlot)

    val userRef = db.collection(UserModel.COLLECTION_NAME).document(userID)
        .collection(BookingModel.COLLECTION_NAME).document(date)

    val batch = db.batch()
    batch.update(userRef, BookingModel.FIELD_STATUS, BookingModel.STATUS_ACCEPTED)
    batch.update(bookingRef, BookingModel.FIELD_STATUS, BookingModel.STATUS_ACCEPTED)
    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}
fun rejectBookingRequest(
    stadiumID: String,
    userID: String,
    date: String,
    timeSlot: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore

    val bookingRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(BookingModel.COLLECTION_NAME).document(date)
        .collection(BookingModel.SUB_COLLECTION_NAME).document(timeSlot)

    val userRef = db.collection(UserModel.COLLECTION_NAME).document(userID)
        .collection(BookingModel.COLLECTION_NAME).document(date)

    val batch = db.batch()
    batch.update(userRef, BookingModel.FIELD_STATUS, BookingModel.STATUS_REJECTED)
    batch.delete(bookingRef)
    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)

}

fun getUserBookingRequestsFromFirestore(
    user: UserModel,
    onSuccessListener: OnSuccessListener<QuerySnapshot>,
    onFailureListener: OnFailureListener
){
    val db = Firebase.firestore
    val collection = db.collection(UserModel.COLLECTION_NAME)
        .document(user.id!!)
        .collection(BookingModel.COLLECTION_NAME)
        .orderBy(BookingModel.FIELD_DATE_TIME, Query.Direction.ASCENDING)
    collection.get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun removeBookingFromFirestore(
    timeSlot: String,
    stadiumID: String,
    date: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {

    val db = Firebase.firestore
    // Get a reference to the booking document
    val bookingRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(BookingModel.COLLECTION_NAME)
        .document(date)
        .collection(BookingModel.SUB_COLLECTION_NAME)
        .document(timeSlot)

    // Delete the booking document
    bookingRef.delete()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getBookedTimesFromFirestore(
    stadiumID: String,
    date: String,
) :CollectionReference {
    val db = Firebase.firestore
    val slotsRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(BookingModel.COLLECTION_NAME)
        .document(date)
        .collection(BookingModel.SUB_COLLECTION_NAME)
        return slotsRef
}

fun playerDocumentExists(
    stadiumID: String,
    userID: String,
    onSuccessListener: OnSuccessListener<Boolean>,
    onFailureListener: OnFailureListener
) {
    // Get a reference to the Firestore database
    val db = Firebase.firestore

    // Get a reference to the PlayersCounter document
    val findPlayersRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS)
        .document(StadiumModel.DOCUMENT_PLAYERS)

    // Query the document for the "players" field and check if it contains the userID
    findPlayersRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            val playersList = document.get(StadiumModel.FIELD_PLAYERS_LIST) as List<String>
            onSuccessListener.onSuccess(playersList.contains(userID))
        } else {
            onSuccessListener.onSuccess(false)
        }

    }
        .addOnFailureListener(onFailureListener)
}


fun setPlayerDataAndUpdateCounter(
    stadiumID: String,
    userID: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val findPlayersRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS)
        .document(StadiumModel.DOCUMENT_PLAYERS)

    findPlayersRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val counter = document.getLong(StadiumModel.FIELD_PLAYERS_COUNTER) ?: 0
                findPlayersRef.update(
                    StadiumModel.FIELD_PLAYERS_COUNTER, counter + 1,
                    StadiumModel.FIELD_PLAYERS_LIST, FieldValue.arrayUnion(userID)
                )
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener)


            } else {
                findPlayersRef.set(
                    hashMapOf(
                        StadiumModel.FIELD_PLAYERS_COUNTER to 1,
                        StadiumModel.FIELD_PLAYERS_LIST to mutableListOf(userID)
                    )
                )
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener)
            }
        }
        .addOnFailureListener(onFailureListener)
}

fun checkCounterInFirestore(
    stadiumID: String,
    onSuccessListener: OnSuccessListener<Boolean>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val findPlayersRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS)
        .document(StadiumModel.DOCUMENT_PLAYERS)

    findPlayersRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val count = document.getLong(StadiumModel.FIELD_PLAYERS_COUNTER) ?: 0
                onSuccessListener.onSuccess(count.toInt() == 3)
            } else {
                onSuccessListener.onSuccess(false)
            }
        }
        .addOnFailureListener(onFailureListener)
}

fun getPlayersIdListFromFirestore(
    stadiumID: String,
    onSuccessListener: OnSuccessListener<List<String>>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val playersCounterRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS)
        .document(StadiumModel.DOCUMENT_PLAYERS)

    playersCounterRef.get()
        .addOnSuccessListener { document ->
            val playerIDs = document.get(StadiumModel.FIELD_PLAYERS_LIST) as List<String>
            onSuccessListener.onSuccess(playerIDs)
        }
        .addOnFailureListener(onFailureListener)
}

fun resetCounterAndRemovePlayers(
    stadiumID: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val findPlayersRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS)
        .document(StadiumModel.DOCUMENT_PLAYERS)
    findPlayersRef.delete()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)

}

fun removePlayer(
    stadiumID: String,
    userID: String,
    onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener
) {
    val db = Firebase.firestore
    val findPlayersRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS)
        .document(StadiumModel.DOCUMENT_PLAYERS)

    findPlayersRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val count = document.getLong(StadiumModel.FIELD_PLAYERS_COUNTER)
                findPlayersRef.update(
                    StadiumModel.FIELD_PLAYERS_COUNTER,
                    count!! - 1,
                    StadiumModel.FIELD_PLAYERS_LIST,
                    FieldValue.arrayRemove(userID)
                )
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener)

            } else {
                onFailureListener.onFailure(Exception("PlayersCounter document does not exist"))
            }
        }
        .addOnFailureListener(onFailureListener)
}