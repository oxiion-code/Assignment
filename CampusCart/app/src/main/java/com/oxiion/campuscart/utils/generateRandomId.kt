package com.oxiion.campuscart.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

fun generateRandomId(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit,cause:String,email:String) {
    val chars="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val db=FirebaseFirestore.getInstance()
    val collection="UniqueCodes"
    fun generateId(): String {
        return (1..6)
            .map { chars.random() } // Generate a list of random characters
            .joinToString("")       // Join them into a single string without separators
    }
    val newId=generateId()
    db.collection(collection).document(newId).get()
        .addOnSuccessListener { document->
            if (document.exists()){
                generateRandomId(onSuccess,onFailure,cause,email)
            }else{
                val newIdData=mapOf("id" to newId,"cause" to cause,"email" to email)
                db.collection(collection).document(newId).set(newIdData)
                    .addOnSuccessListener {
                        onSuccess(newId)
                    }
                    .addOnFailureListener{exception->
                        onFailure(exception)
                    }
            }
        }
        .addOnFailureListener{exception->
            Log.i("id generation ", exception.localizedMessage?.toString() ?: "unknown error")
            onFailure(exception)
        }
}