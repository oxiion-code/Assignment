package com.oxiion.campuscart_user.domain.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oxiion.campuscart_user.data.model.User
import com.oxiion.campuscart_user.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
):AuthRepository {
    override suspend fun signin(email: String, password: String): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun signup(user: User, password: String): Result<String> {
        return try {
            Result.success("")
        }catch(e:Exception){
            Log.e("AuthRepositoryImpl", "Error signing up: ${e.message}", e)
            Result.failure(Exception("Error signing up: ${e.message}"))
        }
    }

    override suspend fun getCollageList(): Result<List<String>> {
        return try{
            val snapshot=firestore.collection("CollegeList").get().await()
            val collageNames=snapshot.documents.mapNotNull { it.getString("college") }
            Result.success(collageNames)
        }catch (e:Exception){
            Result.failure(e)
        }
    }

    override suspend fun getHostelList(college: String): Result<List<String>> {
        return try {
            // Reference to the "CollegeList" collection
            val querySnapshot = firestore.collection("CollegeList")
                .whereEqualTo("college", college)
                .get()
                .await()

            // Check if a document matching the college name exists
            val document = querySnapshot.documents.firstOrNull()

            if (document != null) {
                // Extract the "hostels" field, which is expected to be an array
                val hostels = document.get("hostels") as? List<String> ?: emptyList()
                Result.success(hostels)
            } else {
                // No matching college found
                Result.failure(Exception("No college found with the name: $college"))
            }
        } catch (e: Exception) {
            // Handle exceptions and return an error result
            Result.failure(e)
        }
    }
}