package com.oxiion.campuscart.domain.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.data.models.roles.Admin
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val auth:FirebaseAuth,
    private val firestore: FirebaseFirestore
):AdminRepository {
    override suspend fun signIn(
       admin: Admin,
       password: String
    ): Result<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(admin.email,password).await()
            val userAdmin = auth.currentUser
            if (userAdmin != null) {
                Log.i("abcd1234","inserted into userAdmin")
                firestore.collection("admins").document(userAdmin.uid).set(admin).await()
                Log.i("abcd1234","hey try fn is okk and sucessfull")
            }
            Result.success(true)
        } catch (e: Exception) {
            Log.i("firestore error",e.message.toString())
            Result.failure(e)
        }
    }

    override suspend fun login(userEmail: String, password: String): Result<String> {
        return try {
            // Sign in with email and password
            val userAdmin = auth.signInWithEmailAndPassword(userEmail, password).await()
            val uid = userAdmin.user?.uid ?: return Result.failure(Exception("No user UID found"))
            Result.success(uid) // Return only the UID on successful login
        } catch (e: Exception) {
            Log.e("AdminRepository", "Login failed: ${e.message}")
            Result.failure(e)
        }
    }
    override suspend fun fetchAdminData(uid: String): Result<Admin> {
        return try {
            // Fetch admin data from Firestore
            val documentSnapshot = firestore.collection("admins").document(uid).get().await()
            if (documentSnapshot.exists()) {
                val adminData = documentSnapshot.toObject(Admin::class.java)
                if (adminData != null) {
                    Result.success(adminData) // Return the fetched Admin data
                } else {
                    Result.failure(Exception("Failed to parse admin data"))
                }
            } else {
                Result.failure(Exception("No admin data found for the current user"))
            }
        } catch (e: Exception) {
            Log.e("AdminRepository", "Failed to fetch admin data: ${e.message}")
            Result.failure(e)
        }
    }
    override suspend fun addProduct(product: Product): Result<Boolean> {
        return try {
            val userAdmin =auth.currentUser
            val uid=userAdmin?.uid?:return Result.failure(Exception("No user UID found"))
            val adminDocRef=firestore.collection("admins").document(uid)
            val adminSnapshot=adminDocRef.get().await()
            if (adminSnapshot.exists()){
                val adminData=adminSnapshot.toObject(Admin::class.java)
                if (adminData!=null){
                    val updatedStockItems=adminData.stockItems.toMutableList().apply {
                        add(product)
                    }
                    adminDocRef.update("stockItems",updatedStockItems).await()
                    Result.success(true)
                }else{
                    Result.failure(Exception("Admin data is null"))
                }
            }else{
                Result.failure(Exception("Admin document not found"))
            }
        }catch (e:Exception){
            Log.e("AdminRepository","Failed to add product:${e.message.toString()}")
            Result.failure(e)
        }
    }
    override suspend fun logout(): Result<Boolean>{
    return try{
        auth.signOut()
        Result.success(true)
    }catch(e:Exception){
        Result.failure(e)
    }
}
}