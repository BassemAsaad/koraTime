package com.example.koratime.database

import android.net.Uri
import android.util.Log
import com.example.koratime.model.RoomModel
import com.example.koratime.model.StadiumModel
import com.example.koratime.model.UserModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.UUID

fun addUser_toFirestore(user : UserModel,
                        onSuccessListener: OnSuccessListener<Void>,
                        onFailureListener: OnFailureListener){
    val database = Firebase.firestore
        .collection(UserModel.collectionName)
        .document(user.id!!)
        .set(user)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}
fun getUserData_forLogin(uid: String?,
                         onSuccessListener: OnSuccessListener<DocumentSnapshot>,
                         onFailureListener: OnFailureListener){
    val database = Firebase.firestore
        .collection(UserModel.collectionName)
        .document(uid!!)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun updateLocationInFirestore(userId: String, latitude: Double, longitude: Double, cityName: String) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection(UserModel.collectionName).document(userId)
    userRef.update(
        mapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "city" to cityName
        )
    ).addOnSuccessListener {
        // Handle successful update
        Log.e("Firestore", "Location updated in Firestore")
    }.addOnFailureListener {
        // Handle failure
        Log.e("Firestore", "Error updating location in Firestore")
    }
}


fun addRoomToFirestore(
    room:RoomModel,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener)
{
    val db = Firebase.firestore
    val collection = db.collection(RoomModel.COLLECTION_NAME)
    val document = collection.document()
    room.id = collection.id
    document.set(room)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun uploadImageToStorage(
    imageUri: Uri?,
    onSuccessListener: OnSuccessListener<Uri>,
    onFailureListener: OnFailureListener
) {
    if (imageUri == null) {
        // default image URL
        val defaultImageUrl = Uri.parse("https://firebasestorage.googleapis.com/v0/b/kora-time-d21c3.appspot.com/o/images%2Fgroup_profile.png?alt=media&token=68cbdd0e-43f2-4634-9ba9-bdcdec71555d")
        onSuccessListener.onSuccess(defaultImageUrl)
        return
    }
    // upload the user selected image
    val storage = Firebase.storage
    val storageRef = storage.reference.child("images/${UUID.randomUUID()}")
    val uploadImage = storageRef.putFile(imageUri)
    uploadImage.addOnSuccessListener {
        storageRef.downloadUrl.addOnSuccessListener{ uri -> onSuccessListener.onSuccess(uri) }
            .addOnFailureListener(onFailureListener)
        }
        .addOnFailureListener(onFailureListener)
}
fun getRoomsFromFirestore(
    onSuccessListener: OnSuccessListener<QuerySnapshot>,
    onFailureListener: OnFailureListener)
{
    val db = Firebase.firestore
        .collection(RoomModel.COLLECTION_NAME)
        .orderBy("createdTimestamp", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}
fun getUsersFromFirestore(
    currentUserId: String?,
    onSuccessListener: OnSuccessListener<QuerySnapshot>,
    onFailureListener: OnFailureListener)
{
    val db = Firebase.firestore
        .collection(UserModel.collectionName)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}