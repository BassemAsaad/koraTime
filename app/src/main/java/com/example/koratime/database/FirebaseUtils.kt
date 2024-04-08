package com.example.koratime.database

import android.util.Log
import android.widget.Toast
import com.example.koratime.model.UserModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

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