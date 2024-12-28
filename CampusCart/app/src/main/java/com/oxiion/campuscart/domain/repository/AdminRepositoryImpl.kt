package com.oxiion.campuscart.domain.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.data.models.roles.Admin
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AdminRepository {
    override suspend fun signIn(
        admin: Admin,
        password: String
    ): Result<String> {
        return try {
            // Create a new user with email and password
            auth.createUserWithEmailAndPassword(admin.email, password).await()
            val userAdmin = auth.currentUser

            if (userAdmin != null) {
                // Log for debugging
                Log.i("AdminRepository", "User registered successfully with UID: ${userAdmin.uid}")

                // Save the admin data to Firestore
                firestore.collection("admins").document(userAdmin.uid).set(admin).await()
                 val collegeData= mapOf(
                     "admin id" to userAdmin.uid,
                     "college" to admin.collagename
                 )
                firestore.collection("CollegeList").document(userAdmin.uid).set(collegeData)
                // Log success message
                Log.i("AdminRepository", "Admin data saved successfully in Firestore")

                // Return the UID of the newly created user
                Result.success(userAdmin.uid)
            } else {
                // Handle the case where userAdmin is null
                Log.e("AdminRepository", "User registration succeeded but UID is null")
                Result.failure(Exception("User registration succeeded but UID is null"))
            }
        } catch (e: FirebaseAuthWeakPasswordException) {
            Log.e("AdminRepository", "Weak password: ${e.message}")
            Result.failure(Exception("Password is too weak"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("AdminRepository", "Invalid email: ${e.message}")
            Result.failure(Exception("Invalid email format"))
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("AdminRepository", "Email already in use: ${e.message}")
            Result.failure(Exception("This email is already in use"))
        } catch (e: Exception) {
            Log.e("AdminRepository", "Sign-in failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun login(userEmail: String, password: String,key: String): Result<String> {
        return try {
            // Sign in with email and password
            val userAdmin = auth.signInWithEmailAndPassword(userEmail, password).await()
            val uid = userAdmin.user?.uid ?: return Result.failure(Exception("No user UID found"))

            // Fetch the user document from Firestore
            val userDocument = firestore.collection("admins").document(uid).get().await()
            val keyAdmin=firestore.collection("UniqueCodes").document(key).get().await()

            if (userDocument.exists() && keyAdmin.exists()) {
                val role = userDocument.getString("role")
                val gotEmail=keyAdmin.getString("email")
                if (role == "admin" && userEmail==gotEmail) {
                    Result.success(uid) // Return UID for successful admin login
                } else {
                    // Logout the user and return an error
                    auth.signOut()
                    Result.failure(Exception("Unauthorized access: This account is not an admin"))
                }
            } else {
                // Logout the user and return an error
                auth.signOut()
                Result.failure(Exception("No admin data found for this user"))
            }
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

    override suspend fun generateAdminKey(key: String): Result<Boolean> {
        return try {
            // Get the current user's UID from FirebaseAuth
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                ?: return Result.failure(Exception("Current user UID is null"))

            // Update the "securityCode" field in Firestore
            firestore.collection("admins").document(currentUserUid)
                .update("securityCode", key)
                .await()
            Log.e("Firestore updated key", "Successfully updated security key")
            // Return success if the operation completes
            Result.success(true)
        } catch (e: Exception) {
            // Log the error and return failure
            Log.e("Firestore Update Error", e.message ?: "Unknown Error")
            Result.failure(e)
        }
    }
    override suspend fun logout(): Result<Boolean> {
        return try {
            auth.signOut()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}