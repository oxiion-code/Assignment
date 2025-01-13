package com.oxiion.campuscart_user.utils

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun generateOTP():Result<String>{
    val otp= (1..6).map { (0..9).random() }.joinToString("")
    val instance=FirebaseFirestore.getInstance()
    return try {
        instance.collection("Otp").document(otp).set(mapOf("otp" to otp)).await()
        Result.success(otp)
    }catch (e:Exception) {
        Result.failure(e)
    }
}
suspend fun deleteOTP(otp:String):Result<Boolean>{
    val instance=FirebaseFirestore.getInstance()
    return try {
        instance.collection("Otp").document(otp).delete().await()
        Result.success(true)
    }catch (e:Exception){
        Result.failure(e)
    }
}