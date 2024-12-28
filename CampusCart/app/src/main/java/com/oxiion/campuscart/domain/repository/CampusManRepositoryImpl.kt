package com.oxiion.campuscart.domain.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.data.models.roles.Admin
import com.oxiion.campuscart.data.models.roles.CampusMan
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CampusManRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : CampusManRepository {
    override suspend fun addMember(campusMan: CampusMan): Result<Boolean> {
        return try {
            val userAdmin = auth.currentUser
            val uid = userAdmin?.uid ?: return Result.failure(Exception("No user found"))
            val adminDocRef = firestore.collection("admins").document(uid)
            val adminSnapshot = adminDocRef.get().await()
            if (adminSnapshot.exists()) {
                val adminData = adminSnapshot.toObject(Admin::class.java)
                if (adminData != null) {
                    val updatedEmployeeList = adminData.employeeList.toMutableList().apply {
                        add(campusMan)
                    }
                    adminDocRef.update("employeeList", updatedEmployeeList)
                    Result.success(true)
                } else {
                    Result.failure(Exception("Admin data is not available"))
                }
            } else {
                Result.failure(Exception("Admin document not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMember(campusMan: CampusMan): Result<Boolean> {
        return try {
            val userAdmin = auth.currentUser
            val uid = userAdmin?.uid ?: return Result.failure(Exception("No user found"))
            val adminDocRef = firestore.collection("admins").document(uid)
            val adminSnapshot = adminDocRef.get().await()
            if (adminSnapshot.exists()) {
                val adminData = adminSnapshot.toObject(Admin::class.java)
                if (adminData != null) {
                    val updatedEmployeeList = adminData.employeeList.map {
                        if (it.id == campusMan.id) campusMan else it
                    }
                    adminDocRef.update("employeeList", updatedEmployeeList)
                    Result.success(true)
                } else {
                    Result.failure(Exception("Admin data is not available"))
                }
            } else {
                Result.failure(Exception("Admin document not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMember(campusManId: String): Result<Boolean> {
        return try {
            val userAdmin = auth.currentUser
            val uid = userAdmin?.uid ?: return Result.failure(Exception("No user UID found"))
            val adminDocRef = firestore.collection("admins").document(uid)
            val adminSnapshot = adminDocRef.get().await()
            if (adminSnapshot.exists()) {
                val adminData = adminSnapshot.toObject(Admin::class.java)
                if (adminData != null) {
                    val updatedStockItems = adminData.employeeList.filter { it.id != campusManId }
                    adminDocRef.update("employeeList", updatedStockItems).await()
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

    override suspend fun addStockItemToCampusMan(
        campusManId: String,
        product: Product
    ): Result<Boolean> {
        return try {
            val userAdmin = auth.currentUser
            val uid = userAdmin?.uid ?: return Result.failure(Exception("No user UID found"))
            val adminDocRef = firestore.collection("admins").document(uid)
            val adminSnapshot = adminDocRef.get().await()

            if (adminSnapshot.exists()) {
                val adminData = adminSnapshot.toObject(Admin::class.java)
                if (adminData != null) {
                    // Find the specific CampusMan and add the stock item
                    val updatedEmployeeList = adminData.employeeList.map { campusMan ->
                        if (campusMan.id == campusManId) {
                            campusMan.copy(
                                memberStockItems = campusMan.memberStockItems.toMutableList()
                                    .apply { add(product) }
                            )
                        } else campusMan
                    }

                    // Update the employee list in Firestore
                    adminDocRef.update("employeeList", updatedEmployeeList).await()
                    Result.success(true)
                } else {
                    Result.failure(Exception("Admin data is null"))
                }
            } else {
                Result.failure(Exception("Admin document not found"))
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Failed to add stock item: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getCampusManById(campusManId: String): CampusMan? {
        val userAdmin = auth.currentUser
        val uid = userAdmin?.uid ?: return null
        val adminDocRef = firestore.collection("admins").document(uid)
        val adminSnapshot = adminDocRef.get().await()

        return if (adminSnapshot.exists()) {
            val adminData = adminSnapshot.toObject(Admin::class.java)
            adminData?.employeeList?.find { it.id == campusManId }
        } else {
            null
        }
    }

}