package com.example.koratime.database

import android.net.Uri
import android.util.Log
import com.example.koratime.model.FriendModel
import com.example.koratime.model.MessageModel
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

fun getUserForLogin(uid: String?,
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
                          onFailureListener: OnFailureListener)
{
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
    room.id = document.id
    document.set(room)
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


fun addFriendRequestToFirestore(sender: String,
                                receiver: String,
                                senderPicture : String?,
                                senderUserName : String?,
                                onSuccessListener: OnSuccessListener<Void>,
                                onFailureListener: OnFailureListener) {

    val db = Firebase.firestore
    val request = FriendModel(
        senderID = sender,
        receiverID = receiver,
        status = "pending",
        senderName = senderUserName,
        senderProfilePicture = senderPicture
        )

    // Add the friend request to receiverUser
    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
        .document(receiver)
        .collection(FriendModel.COLLECTION_NAME_RECEIVER)
        .document()
    // Add the friend request to senderUser
    val senderRef = db.collection(UserModel.COLLECTION_NAME)
        .document(sender)
        .collection(FriendModel.COLLECTION_NAME_SENDER)
        .document()
    // set id
    request.requestID = senderRef.id

    // batch write can improve performance and reduce the risk of data inconsistency.
    val batch = db.batch()
    batch.set(senderRef, request)
    batch.set(receiverRef, request)

    batch.commit()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun checkFriendRequestStatus(sender: String,
                             receiver: String,
                             callback: (String) -> Unit ) {
    val db = Firebase.firestore
    val receiverRef = db.collection(UserModel.COLLECTION_NAME)
    receiverRef
        .document(sender)
        .collection(FriendModel.COLLECTION_NAME_SENDER)
        .whereEqualTo("senderID", sender)
        .whereEqualTo("receiverID", receiver)
        .whereEqualTo("status", "pending")
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // There's a pending friend request from current user to target user
                callback("pending")
            } else {
                // No pending friend request found
                callback("not_pending")
            }
        }
        .addOnFailureListener { e ->
            // Error handling
            Log.e("Firestore", "Error checking friend request status", e)
            callback("error")
        }
}
fun getFriendRequestsFromFirestore(receiver: String?,
                                   onSuccessListener: OnSuccessListener<QuerySnapshot>,
                                   onFailureListener: OnFailureListener){
    val db = Firebase.firestore
    val collectionRef = db.collection(UserModel.COLLECTION_NAME)
    val receiverRef = collectionRef.document(receiver!!)
    receiverRef.collection(FriendModel.COLLECTION_NAME_RECEIVER)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)

}
fun removeFriendRequestFromFirestore(sender: String, receiver: String, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {

    val db = Firebase.firestore

    // Create a query to find the friend request document in the receiver's collection of pending friend requests
    val receiverQuery = db.collection(UserModel.COLLECTION_NAME)
        .document(receiver)
        .collection(FriendModel.COLLECTION_NAME_RECEIVER)
        .whereEqualTo("senderID", sender)
        .whereEqualTo("status", "pending")

    // Create a query to find the friend request document in the sender's collection of sent friend requests
    val senderQuery = db.collection(UserModel.COLLECTION_NAME)
        .document(sender)
        .collection(FriendModel.COLLECTION_NAME_SENDER)
        .whereEqualTo("receiverID", receiver)
        .whereEqualTo("status", "pending")

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

fun addMessageToFirestore(
    message: MessageModel,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener){

    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
        .document(message.roomID!!)
        .collection(MessageModel.COLLECTION_NAME)
    val messageRef = collection.document()
    message.messageID =messageRef.id
    messageRef.set(message)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}
fun getMessageFromFirestore(roomId : String): CollectionReference {
    val db = Firebase.firestore
    val collectionRef = db.collection(RoomModel.COLLECTION_NAME)
    val roomRef = collectionRef.document(roomId)
    return roomRef.collection(MessageModel.COLLECTION_NAME)
}


fun addStadiumToFirestore(stadium:StadiumModel,
                          onSuccessListener: OnSuccessListener<Void>,
                          onFailureListener: OnFailureListener) {
    val db = Firebase.firestore
    val collection = db.collection(StadiumModel.COLLECTION_NAME)
    val document = collection.document()
    stadium.id = document.id
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
