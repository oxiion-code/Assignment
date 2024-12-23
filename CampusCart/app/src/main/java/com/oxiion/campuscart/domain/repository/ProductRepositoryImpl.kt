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
class ProductRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) :ProductRepository{
    override suspend fun addProduct(product: Product): Result<Boolean> {
        return try {
            val userAdmin = auth.currentUser
            val uid = userAdmin?.uid ?: return Result.failure(Exception("No user UID found"))
            val adminDocRef = firestore.collection("admins").document(uid)
            val adminSnapshot = adminDocRef.get().await()
            if (adminSnapshot.exists()) {
                val adminData = adminSnapshot.toObject(Admin::class.java)
                if (adminData != null) {
                    val updatedStockItems = adminData.stockItems.toMutableList().apply {
                        add(product)
                    }
                    adminDocRef.update("stockItems", updatedStockItems).await()
                    Result.success(true)
                } else {
                    Result.failure(Exception("Admin data is null"))
                }
            } else {
                Result.failure(Exception("Admin document not found"))
            }
        } catch (e: Exception) {
            Log.e("AdminRepository", "Failed to add product:${e.message.toString()}")
            Result.failure(e)
        }
    }
    override suspend fun updateProduct(product: Product): Result<Boolean> {
        return try {
            val userAdmin = auth.currentUser
            val uid = userAdmin?.uid ?: return Result.failure(Exception("No user UID found"))
            val adminDocRef = firestore.collection("admins").document(uid)
            val adminSnapshot = adminDocRef.get().await()
            if (adminSnapshot.exists()) {
                val adminData = adminSnapshot.toObject(Admin::class.java)
                if (adminData != null) {
                    val updatedStockItems = adminData.stockItems.map {
                        if (it.id == product.id) product else it
                    }
                    adminDocRef.update("stockItems", updatedStockItems).await()
                    Result.success(true)
                } else {
                    Result.failure(Exception("Admin data is null"))
                }
            } else {
                Result.failure(Exception("Admin document not found"))
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Failed to update product: ${e.message}")
            Result.failure(e)
        }
    }
    override suspend fun deleteProduct(productId: String): Result<Boolean> {
        return try {
            val userAdmin = auth.currentUser
            val uid = userAdmin?.uid ?: return Result.failure(Exception("No user UID found"))
            val adminDocRef = firestore.collection("admins").document(uid)
            val adminSnapshot = adminDocRef.get().await()
            if (adminSnapshot.exists()) {
                val adminData = adminSnapshot.toObject(Admin::class.java)
                if (adminData != null) {
                    val updatedStockItems = adminData.stockItems.filter { it.id != productId }
                    adminDocRef.update("stockItems", updatedStockItems).await()
                    Result.success(true)
                } else {
                    Result.failure(Exception("Admin data is null"))
                }
            } else {
                Result.failure(Exception("Admin document not found"))
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Failed to delete product: ${e.message}")
            Result.failure(e)
        }
    }

}