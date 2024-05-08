package com.example.koratime.database

import android.net.Uri
import android.util.Log
import com.example.koratime.model.FriendMessageModel
import com.example.koratime.model.FriendModel
import com.example.koratime.model.FriendRequestModel
import com.example.koratime.model.RoomMessageModel
import com.example.koratime.model.RoomModel
import com.example.koratime.model.StadiumModel
import com.example.koratime.model.UserModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.UUID

fun addUserToFirestore(user : UserModel,
                       onSuccessListener: OnSuccessListener<Void>,
                       onFailureListener: OnFailureListener){
    val db = Firebase.firestore
    val collection = db.collection(UserModel.COLLECTION_NAME)
    collection
        .document(user.id!!)
        .set(user)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getUserFromFirestore(uid: String?,
                         onSuccessListener: OnSuccessListener<DocumentSnapshot>,
                         onFailureListener: OnFailureListener){
    val db = Firebase.firestore
    val collection = db.collection(UserModel.COLLECTION_NAME)
    collection
        .document(uid!!)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getUsersFromFirestore(currentUserId: String?,
                          onSuccessListener: OnSuccessListener<QuerySnapshot>,
                          onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val collection =db.collection(UserModel.COLLECTION_NAME)
    collection
        .whereNotEqualTo("id", currentUserId)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun updateUserLocationInFirestore(userId: String,
                                  latitude: Double,
                                  longitude: Double,
                                  cityName: String,
                                  onSuccessListener: OnSuccessListener<Void>,
                                  onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val userRef = db.collection(UserModel.COLLECTION_NAME).document(userId)
    userRef.update(mapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "city" to cityName
        ))
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun addRoomToFirestore(room:RoomModel,
                       onSuccessListener: OnSuccessListener<Void>,
                       onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
    val document = collection.document()
    room.roomID = document.id
    document.set(room)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun removeRoomFromFirestore(roomId: String,
                            onSuccessListener: OnSuccessListener<Void>,
                            onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
    val document = collection.document(roomId)
    document.delete()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getAllRoomsFromFirestore(onSuccessListener: OnSuccessListener<QuerySnapshot>,
                             onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
    collection
        .orderBy("createdTimestamp", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getUserRoomsFromFirestore(userId: String,
                              onSuccessListener: OnSuccessListener<QuerySnapshot>,
                              onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val collection= db.collection(RoomModel.COLLECTION_NAME)
    collection
        .whereEqualTo("userManager", userId)
        .orderBy("createdTimestamp", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun addStadiumRoomToFirestore(stadium: StadiumModel,
                              playerIds:List<String>,
                              onSuccessListener: OnSuccessListener<Void>,
                              onFailureListener: OnFailureListener) {

    val room = RoomModel(
        name = StadiumModel.COLLECTION_NAME,
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

fun getStadiumRoomFromFirestore(playerID:String,
                                onSuccessListener: OnSuccessListener<QuerySnapshot>,
                                onFailureListener: OnFailureListener) {

    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
    val query = collection.whereArrayContains("playerIds", playerID)
    query.get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun addRoomMessageToFirestore(message: RoomMessageModel,
                              onSuccessListener: OnSuccessListener<Void>,
                              onFailureListener: OnFailureListener){

    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
        .document(message.roomID!!)
        .collection(RoomMessageModel.COLLECTION_NAME)
    val messageRef = collection.document()
    message.messageID =messageRef.id
    messageRef.set(message)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getRoomMessagesFromFirestore(roomId : String): CollectionReference {
    val db = Firebase.firestore
    val roomRef = db.collection(RoomModel.COLLECTION_NAME)
        .document(roomId)

    return roomRef.collection(RoomMessageModel.COLLECTION_NAME)
}

fun uploadImageToStorage(imageUri: Uri?,
                         onSuccessListener: OnSuccessListener<Uri>,
                         onFailureListener: OnFailureListener) {
    if (imageUri == null) {
        // default image URL
        val defaultImageUrl = Uri.parse("https://firebasestorage.googleapis.com/v0/b/kora-time-d21c3.appspot.com/o/images%2Fgroup_profile.png?alt=media&token=68cbdd0e-43f2-4634-9ba9-bdcdec71555d")
        onSuccessListener.onSuccess(defaultImageUrl)
    } else{
        // upload the user selected image
        val storage = Firebase.storage
        val storageRef = storage.reference.child("images/${UUID.randomUUID()}")
        val uploadImage = storageRef.putFile(imageUri)

        uploadImage
            .addOnSuccessListener {
                storageRef.downloadUrl
                    .addOnSuccessListener{ uri -> onSuccessListener.onSuccess(uri) }
                    .addOnFailureListener(onFailureListener)
            }
            .addOnFailureListener(onFailureListener)
    }
}

fun uploadMultipleImagesToStorage(uris: List<Uri>,
                                  stadiumID: String,
                                  onSuccessListener: OnSuccessListener<List<String>>,
                                  onFailureListener: OnFailureListener) {
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

fun addMultipleImagesToFirestore(imageUris: List<String>,
                                 stadiumID:String,
                                 onSuccessListener: OnSuccessListener<Void>,
                                 onFailureListener: OnFailureListener) {
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

fun getMultipleImagesFromFirestore(stadiumID: String,
                                   onSuccessListener: OnSuccessListener<List<String>>,
                                   onFailureListener: OnFailureListener) {
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

fun addFriendRequestToFirestore(sender: String,
                                receiver: String,
                                senderPicture : String?,
                                senderUserName : String?, onSuccessListener: OnSuccessListener<Void>,
                                onFailureListener: OnFailureListener) {

    val requestSender = FriendRequestModel(
        senderID = sender,
        receiverID = receiver,
        status = "pending",
        senderName = senderUserName,
        senderProfilePicture = senderPicture
        )

    val db = Firebase.firestore
    // create a unique ID for the friend request
    val requestId = db.collection(UserModel.COLLECTION_NAME)
        .document().id
    // Add the friend request to receiverUser
    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
        .document(receiver)
        .collection(FriendRequestModel.SUB_COLLECTION_NAME_RECEIVER)
        .document(requestId)
    // Add the friend request to senderUser
    val senderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(sender)
        .collection(FriendRequestModel.SUB_COLLECTION_NAME_SENDER)
        .document(requestId)
    // set id
    requestSender.requestID = requestId

    // batch write can improve performance and reduce the risk of data inconsistency.
    val batch = db.batch()
    batch.set(senderRef, requestSender)
    batch.set(receiverRef, requestSender)

    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun checkFriendRequestStatusFromFirestoreSender(sender: String,
                                                receiver: String,
                                                callback: (String) -> Unit ) {
    val db = Firebase.firestore
    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
    receiverRef
        .document(sender)
        .collection(FriendRequestModel.SUB_COLLECTION_NAME_SENDER)
        .whereEqualTo("senderID", sender)
        .whereEqualTo("receiverID", receiver)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Iterate over the documents in the query snapshot
                for (document in querySnapshot.documents) {
                    // Get the status of the friend request
                    val status = document.getString("status")
                    if (status == "pending") {
                        // callback function to handle the result of the asynchronous operation.
                        callback("pending")
                    } else if (status == "accepted") {
                        callback("accepted")
                    }
                }
            }
            else {
                // No friend request found
                callback("not_pending")
            }
        }
        .addOnFailureListener { e ->
            // Error handling
            Log.e("Firestore", "Error checking friend request status", e)
            callback("error")
        }
}
fun checkFriendRequestStatusFromFirestoreReceiver(currentUser: String,
                                                senderID: String,
                                                callback: (String) -> Unit ) {
    val db = Firebase.firestore
    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
    receiverRef
        .document(currentUser)
        .collection(FriendRequestModel.SUB_COLLECTION_NAME_RECEIVER)
        .whereEqualTo("senderID", senderID)
        .whereEqualTo("receiverID", currentUser)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Iterate over the documents in the query snapshot
                for (document in querySnapshot.documents) {
                    // Get the status of the friend request
                    val status = document.getString("status")
                    if (status == "pending") {
                        // callback function to handle the result of the asynchronous operation.
                        callback("pending")
                    } else if (status == "accepted") {
                        callback("accepted")
                    }
                }
            }
            else {
                // No friend request found
                callback("not_pending")
            }
        }
        .addOnFailureListener { e ->
            // Error handling
            Log.e("Firestore", "Error checking friend request status", e)
            callback("error")
        }
}
fun getFriendRequestsFromFirestore(receiver: String,
                                   onSuccessListener: OnSuccessListener<QuerySnapshot>,
                                   onFailureListener: OnFailureListener){
    val db = Firebase.firestore
    val collectionRef = db.collection(UserModel.COLLECTION_NAME)
    val receiverRef = collectionRef.document(receiver)
    receiverRef.collection(FriendRequestModel.SUB_COLLECTION_NAME_RECEIVER)
        .whereEqualTo("status","pending")
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)

}

fun acceptFriendRequest(sender: FriendRequestModel,
                        receiver: UserModel,
                        requestID: String,
                        onSuccessListener: OnSuccessListener<Void>,
                        onFailureListener: OnFailureListener){

    val friendSender = FriendModel(
        friendID = receiver.id,
        friendPicture = receiver.profilePicture,
        friendName = receiver.userName
    )
    val friendReceiver = FriendModel(
        friendID = sender.senderID,
        friendPicture = sender.senderProfilePicture,
        friendName = sender.senderName
    )
    val db = Firebase.firestore

    // create a unique ID for the friend request
    val friendshipID = db.collection(UserModel.COLLECTION_NAME)
        .document().id

    // create friend collection for receiver
    val friendReceiverRef = db.collection(UserModel.COLLECTION_NAME)
        .document(receiver.id!!)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(friendshipID)

    // create friend collection for sender
    val friendSenderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(sender.senderID!!)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(friendshipID)

    // set id
    friendSender.friendshipID = friendshipID
    friendReceiver.friendshipID = friendshipID
    // batch write can improve performance and reduce the risk of data inconsistency.
    val batch = db.batch()
    batch.set(friendReceiverRef, friendReceiver)
    batch.set(friendSenderRef, friendSender)

    batch.commit()
        .addOnSuccessListener{
            updateFriendRequestStatusToAccepted(
                sender = sender.senderID,
                receiver = receiver.id,
                requestId = requestID,
                onSuccessListener = onSuccessListener,
                onFailureListener = onFailureListener
            )
        }
        .addOnFailureListener(onFailureListener)
}

fun updateFriendRequestStatusToAccepted(sender: String,
                                        receiver: String,
                                        requestId: String,
                                        onSuccessListener: OnSuccessListener<Void>,
                                        onFailureListener: OnFailureListener) {

    val db = Firebase.firestore
    // create a unique ID for the friend request
    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
        .document(receiver)
        .collection(FriendRequestModel.SUB_COLLECTION_NAME_RECEIVER)
        .document(requestId)
    // Add the friend request to senderUser
    val senderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(sender)
        .collection(FriendRequestModel.SUB_COLLECTION_NAME_SENDER)
        .document(requestId)

    // batch write can improve performance and reduce the risk of data inconsistency.
    val batch = db.batch()
    batch.update(senderRef, "status", "accepted")
    batch.update(receiverRef, "status", "accepted")

    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getFriendsFromFirestore(userID:String,
                            onSuccessListener: OnSuccessListener<QuerySnapshot>,
                            onFailureListener: OnFailureListener){
    val db = Firebase.firestore
    val friendRef = db.collection(UserModel.COLLECTION_NAME)
    friendRef
        .document(userID)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .whereEqualTo("friendshipStatus",true)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)

}

fun removeFriendRequestWithoutRequestID(sender: String,
                                        receiver: String,
                                        onSuccessListener: OnSuccessListener<Void>,
                                        onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    // Create a query to find the friend request document in the receiver's collection of pending friend requests
    val receiverQuery = db.collection(UserModel.COLLECTION_NAME)
        .document(receiver)
        .collection(FriendRequestModel.SUB_COLLECTION_NAME_RECEIVER)
        .whereEqualTo("senderID", sender)

    // Create a query to find the friend request document in the sender's collection of sent friend requests
    val senderQuery = db.collection(UserModel.COLLECTION_NAME)
        .document(sender)
        .collection(FriendRequestModel.SUB_COLLECTION_NAME_SENDER)
        .whereEqualTo("receiverID", receiver)

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

fun removeFriendRequestWithRequestID(sender: String,
                                     receiver: String,
                                     request: String,
                                     onSuccessListener: OnSuccessListener<Void>,
                                     onFailureListener: OnFailureListener) {

    val db = Firebase.firestore

    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
        .document(receiver)
        .collection(FriendRequestModel.SUB_COLLECTION_NAME_RECEIVER)
        .document(request)

    val senderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(sender)
        .collection(FriendRequestModel.SUB_COLLECTION_NAME_SENDER)
        .document(request)


    // batch write can improve performance and reduce the risk of data inconsistency.
    val batch = db.batch()
    batch.delete(senderRef)
    batch.delete(receiverRef)

    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun removeFriendFromFirestore(senderID: String,
                              receiverID: String,
                              friendshipID: String,
                              onSuccessListener: OnSuccessListener<Void>,
                              onFailureListener: OnFailureListener) {
    val db = Firebase.firestore

    // Get a reference to the friendship document for the sender
    val senderFriendRef = db.collection(UserModel.COLLECTION_NAME)
        .document(senderID)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(friendshipID)

    // Get a reference to the friendship document for the receiver
    val receiverFriendRef = db.collection(UserModel.COLLECTION_NAME)
        .document(receiverID)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(friendshipID)


    // Batch write can improve performance and reduce the risk of data inconsistency.
    val batch = db.batch()
    batch.delete(senderFriendRef)
    batch.delete(receiverFriendRef)
    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun addFriendMessageToFirestore(
    message: FriendMessageModel,
    friendshipID:String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener){

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
    batch.set(senderRef,message)
    batch.set(receiverRef,message)
    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)

}

fun getFriendMessagesFromFirestore(senderID: String,
                                   friendshipID: String): Query {
    val db = Firebase.firestore

    val senderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(senderID)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(friendshipID)


    return senderRef.collection(FriendMessageModel.COLLECTION_NAME)
        .orderBy("dateTime",Query.Direction.ASCENDING)
}

fun getLastMessageFromFirestore(userID:String,
                                friendshipID: String,
                                onSuccessListener: OnSuccessListener<String>,
                                onFailureListener: OnFailureListener){

    val db = Firebase.firestore
    val query = db.collection(UserModel.COLLECTION_NAME)
        .document(userID)
        .collection(FriendModel.SUB_COLLECTION_NAME)
        .document(friendshipID)
        .collection(FriendMessageModel.COLLECTION_NAME)
        .orderBy("dateTime", Query.Direction.DESCENDING)
        .limit(1)

    query
        .get()
        .addOnSuccessListener { documents ->
        for (document in documents) {
            val message = document.toObject(FriendMessageModel::class.java)
            // Access the last content sent
            onSuccessListener.onSuccess(message.content) }
        }
        .addOnFailureListener(onFailureListener)

}

fun addStadiumToFirestore(stadium:StadiumModel,
                          onSuccessListener: OnSuccessListener<Void>,
                          onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val collection = db.collection(StadiumModel.COLLECTION_NAME)
    val document = collection.document()
    stadium.stadiumID = document.id
    document.set(stadium)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getAllStadiumsFromFirestore(onSuccessListener: OnSuccessListener<QuerySnapshot>,
                                onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val collection = db.collection(StadiumModel.COLLECTION_NAME)
    collection
        .orderBy("createdTimestamp", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getUserStadiumFromFirestore(userId: String?,
                                onSuccessListener: OnSuccessListener<QuerySnapshot>,
                                onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val collection= db.collection(StadiumModel.COLLECTION_NAME)
    collection
        .whereEqualTo("userManager", userId)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun deleteStadiumFromFirestore(stadiumID: String,
                               onSuccessListener: OnSuccessListener<Void>,
                               onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val collection = db.collection(StadiumModel.COLLECTION_NAME)
    val document = collection.document(stadiumID)
    document.delete()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun addBookingToFirestore(timeSlot: String,
                          stadiumID:String,
                          date: String,
                          userId: String,
                          onSuccessListener: OnSuccessListener<Void>,
                          onFailureListener: OnFailureListener) {

    val db = Firebase.firestore
    // Add the booking details to Firestore under the respective date document
    val bookingRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection("Bookings")
        .document(date)
        .collection("TimeSlots")
        .document(timeSlot)

    val bookingData = hashMapOf(
        "userId" to userId,
    )
    bookingRef.set(bookingData)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun removeBookingFromFirestore(timeSlot: String,
                               stadiumID: String,
                               date: String,
                               onSuccessListener: OnSuccessListener<Void>,
                               onFailureListener: OnFailureListener) {

    val db = Firebase.firestore
    // Get a reference to the booking document
    val bookingRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection("Bookings")
        .document(date)
        .collection("TimeSlots")
        .document(timeSlot)

    // Delete the booking document
    bookingRef.delete()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getBookedTimesFromFirestore(stadiumID: String,
                                date: String,
                                onSuccessListener: OnSuccessListener<List<String>>,
                                onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val slotsRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection("Bookings")
        .document(date)
        .collection("TimeSlots")

    slotsRef.get()
        .addOnSuccessListener { documents ->
            val slotNames = mutableListOf<String>()
            for (document in documents) {
                slotNames.add(document.id)
            }
            onSuccessListener.onSuccess(slotNames)
        }
        .addOnFailureListener(onFailureListener)
}

fun playerDocumentExists(stadiumID: String,
                         userID: String,
                         onSuccessListener: OnSuccessListener<Boolean>,
                         onFailureListener: OnFailureListener) {
    // Get a reference to the Firestore database
    val db = Firebase.firestore

    // Get a reference to the PlayersCounter document
    val findPlayersRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS).document("PlayersCounter")

    // Get the PlayersCounter document
    findPlayersRef.get()
        .addOnSuccessListener { document ->
            // Check if the PlayersCounter document exists
            if (document.exists()) {
                // Get a reference to the player document
                val playerRef = findPlayersRef.collection("Players").document(userID)

                // Get the player document
                playerRef.get()
                    .addOnSuccessListener { playerDocument ->
                        // Check if the player document exists
                        if (playerDocument.exists()) {
                            // Call the onSuccessListener with true to indicate that the player document exists
                            onSuccessListener.onSuccess(true)
                        } else {
                            // Call the onSuccessListener with false to indicate that the player document does not exist
                            onSuccessListener.onSuccess(false)
                        }
                    }
                    .addOnFailureListener(onFailureListener)
            } else {
                // Call the onSuccessListener with false to indicate that the player document does not exist
                onSuccessListener.onSuccess(false)
            }
        }
        .addOnFailureListener(onFailureListener)
}

fun setPlayerDataAndUpdateCounter(stadiumID: String,
                                  userID: String,
                                  onSuccessListener: OnSuccessListener<Void>,
                                  onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val findPlayersRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS).document("PlayersCounter")

    findPlayersRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val count = document.getLong("count") ?:0
                val playerRef = findPlayersRef.collection("Players").document(userID)
                playerRef.set(hashMapOf("userID" to userID))
                    .addOnSuccessListener {
                        findPlayersRef.update("count", count + 1)
                            .addOnSuccessListener(onSuccessListener)
                            .addOnFailureListener(onFailureListener)
                    }.addOnFailureListener(onFailureListener)

            } else {
                val playerRef = findPlayersRef.collection("Players").document(userID)
                playerRef.set(hashMapOf("userID" to userID))
                    .addOnSuccessListener {
                        findPlayersRef.set(hashMapOf("count" to 1))
                            .addOnSuccessListener(onSuccessListener)
                            .addOnFailureListener(onFailureListener)
                    }
                    .addOnFailureListener(onFailureListener)

            }
        }
        .addOnFailureListener(onFailureListener)
}

fun checkCounterInFirestore(stadiumID: String,
                            onSuccessListener: OnSuccessListener<Boolean>,
                            onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val findPlayersRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS).document("PlayersCounter")

    findPlayersRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val count = document.getLong("count") ?: 0
                onSuccessListener.onSuccess(count.toInt() == 3)
            } else {
                onSuccessListener.onSuccess(false)
            }
        }
        .addOnFailureListener { exception ->
            onFailureListener.onFailure(exception)
        }
}

fun getPlayersIdListFromFirestore(stadiumID: String,
                                  onSuccessListener: OnSuccessListener<List<String>>,
                                  onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val playersCounterRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS).document("PlayersCounter")
        .collection("Players")

    playersCounterRef.get()
        .addOnSuccessListener { documents ->
            val playerIDs = documents.map { it.id }
            onSuccessListener.onSuccess(playerIDs)
        }
        .addOnFailureListener { exception ->
            onFailureListener.onFailure(exception)
        }
}

fun resetCounterAndRemovePlayers(stadiumID: String,
                                 onSuccessListener: OnSuccessListener<String>,
                                 onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val findPlayersRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS)
        .document("PlayersCounter")
    val playersRef = findPlayersRef.collection("Players")


    findPlayersRef.update("count", 0)
        .addOnSuccessListener {
            playersRef.get()
                .addOnSuccessListener {querySnapshot ->
                    querySnapshot.documents.forEach{
                        onSuccessListener.onSuccess(it.id)
                        it.reference.delete()
                    }
                }.addOnFailureListener(onFailureListener)
        }
        .addOnFailureListener(onFailureListener)
}
fun removePlayer(stadiumID: String,
                 userID: String,
                 onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val findPlayersRef = db.collection(StadiumModel.COLLECTION_NAME).document(stadiumID)
        .collection(StadiumModel.SUB_COLLECTION_FIND_PLAYERS).document("PlayersCounter")

    findPlayersRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val count = document.getLong("count") ?:0
                val playerRef = findPlayersRef.collection("Players").document(userID)
                playerRef.delete()
                    .addOnSuccessListener {
                        findPlayersRef.update("count", count - 1)
                            .addOnSuccessListener(onSuccessListener)
                            .addOnFailureListener(onFailureListener)
                    }
                    .addOnFailureListener(onFailureListener)
            } else {
                onFailureListener.onFailure(Exception("PlayersCounter document does not exist"))
            }
        }
        .addOnFailureListener(onFailureListener)
}