package com.example.koratime.database

import android.net.Uri
import com.example.koratime.model.FriendModel
import com.example.koratime.model.MessageModel
import com.example.koratime.model.RoomModel
import com.example.koratime.model.UserModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.UUID

fun addUserToFirestore(user : UserModel,
                       onSuccessListener: OnSuccessListener<Void>,
                       onFailureListener: OnFailureListener){
    val db = Firebase.firestore
    val collection = db.collection(UserModel.collectionName)
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
    val collection = db.collection(UserModel.collectionName)
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
    val collection =db.collection(UserModel.collectionName)
    collection
        .whereNotEqualTo("id", currentUserId)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun updateLocationInFirestore(userId: String, latitude: Double, longitude: Double, cityName: String,
                              onSuccessListener: OnSuccessListener<Void>,
                              onFailureListener: OnFailureListener) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection(UserModel.collectionName).document(userId)
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
    }else{
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

fun addFriendFromFirestore(from: String,
                           to: String,
                           onSuccessListener: OnSuccessListener<DocumentReference>,
                           onFailureListener: OnFailureListener) {

    val db = Firebase.firestore
    val request = FriendModel(from = from, to = to, status = "pending",checkFriendRequest = true)
    // Add the friend request to Firestore
    val collection = db.collection(FriendModel.collectionName)
    collection
        .add(request)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun addMessageToFirestore(
    message: MessageModel,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener){

    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
        .document(message.roomID!!)
        .collection(MessageModel.collectionName)
    val messageRef = collection.document()
    message.messageID =messageRef.id
    messageRef.set(message)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}
fun getMessageFromFirestore(roomId : String): CollectionReference {
    val collectionRef = Firebase.firestore.collection(RoomModel.COLLECTION_NAME)
    val roomRef = collectionRef.document(roomId)
    return roomRef.collection(MessageModel.collectionName)
}