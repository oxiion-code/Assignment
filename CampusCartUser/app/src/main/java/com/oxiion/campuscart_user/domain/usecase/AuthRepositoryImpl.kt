package com.oxiion.campuscart_user.domain.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.oxiion.campuscart_user.data.model.AuthResult
import com.oxiion.campuscart_user.data.model.Product
import com.oxiion.campuscart_user.data.model.User
import com.oxiion.campuscart_user.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun signup(user: User, password: String): Result<AuthResult> {
        return try {
            // Create a new user with email and password
            auth.createUserWithEmailAndPassword(user.address!!.email, password).await()
            val userAdmin = auth.currentUser

            if (userAdmin != null) {
                Log.i("Repository", "User registered successfully with UID: ${userAdmin.uid}")
                firestore.collection("Users").document(userAdmin.uid).set(user).await()

                // Fetch the product list
                fetchProductList(
                    college = user.college,
                    hostelNumber = user.address!!.hostelNumber
                ).fold(
                    onSuccess = { products ->
                        Result.success(AuthResult(uid = userAdmin.uid, products = products))
                    },
                    onFailure = { error ->
                        Result.failure(error)
                    }
                )
            } else {
                Log.e("Repository", "User registration succeeded but UID is null")
                Result.failure(Exception("User registration succeeded but UID is null"))
            }
        } catch (e: Exception) {
            Log.e("Repository", "Sign-up failed: ${e.message}")
            Result.failure(e)
        }
    }


    override suspend fun signin(email: String, password: String): Result<AuthResult> {
        return try {
            // Sign in with email and password
            auth.signInWithEmailAndPassword(email, password).await()
            val userAdmin = auth.currentUser

            if (userAdmin != null) {
                Log.i("Repository", "User signed in successfully with UID: ${userAdmin.uid}")

                // Fetch the user data from the Users collection
                val userDoc = firestore.collection("Users").document(userAdmin.uid).get().await()

                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)

                    if (user != null) {
                        Log.i("Repository", "Fetched user data: $user")

                        // Fetch the product list using the user's college and hostel number
                        fetchProductList(
                            user.college,
                            user.address?.hostelNumber ?: ""
                        ).let { productListResult ->
                            productListResult.fold(
                                onSuccess = { products ->
                                    Result.success(
                                        AuthResult(
                                            uid = userAdmin.uid,
                                            products = products
                                        )
                                    )
                                },
                                onFailure = { error ->
                                    Result.failure(error)
                                }
                            )
                        }
                    } else {
                        Log.e("Repository", "Failed to parse user data")
                        Result.failure(Exception("Failed to fetch user data"))
                    }
                } else {
                    Log.e("Repository", "User data does not exist in Users collection")
                    Result.failure(Exception("User data not found"))
                }
            } else {
                Log.e("Repository", "Sign-in succeeded but UID is null")
                Result.failure(Exception("Sign-in succeeded but UID is null"))
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e("Repository", "Invalid user: ${e.message}")
            Result.failure(Exception("Invalid user credentials"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("Repository", "Invalid credentials: ${e.message}")
            Result.failure(Exception("Invalid email or password"))
        } catch (e: Exception) {
            Log.e("Repository", "Sign-in failed: ${e.message}")
            Result.failure(e)
        }
    }
    // In AuthRepository
    override suspend fun getUserData(uid: String): User? {
        return try {
            // Fetch user data from Firestore
            val userDoc = firestore.collection("Users").document(uid).get().await()
            if (userDoc.exists()) {
                userDoc.toObject(User::class.java) // Map Firestore document to User object
            } else {
                null // Return null if user doesn't exist
            }
        } catch (e: Exception) {
            null // Return null in case of failure
        }
    }


    private suspend fun fetchProductList(
        college: String,
        hostelNumber: String
    ): Result<List<Product>> {
        return try {
            // Query the Admins collection based on the college
            val adminCollectionRef = firestore.collection("admins")
            val adminSnapshot = adminCollectionRef
                .whereEqualTo("collagename", college)
                .get()
                .await()

            if (adminSnapshot.documents.isNotEmpty()) {
                val adminDoc = adminSnapshot.documents.first()
                val employeeList = adminDoc.get("employeeList") as? List<Map<String, Any>>

                // Find the campus man where "address.hostelNumber" matches
                val campusMan = employeeList?.find { employee ->
                    val address = employee["address"] as? Map<String, Any>
                    address?.get("hostelNumber") == hostelNumber
                }

                if (campusMan != null) {
                    val products = campusMan["memberStockItems"] as? List<Map<String, Any>>
                    val productList = products?.map { product ->
                        Product(
                            id = product["id"] as String,
                            name = product["name"] as String,
                            description = product["description"] as String,
                            category = product["category"] as String,
                            price = (product["price"] as Number).toDouble(),
                            quantity = (product["quantity"] as Number).toInt(),
                            isAvailable = product["available"] as Boolean,
                            discount = (product["discount"] as Number).toDouble(),
                            image = product["image"] as String,
                            rating = (product["rating"] as Number).toDouble()
                        )
                    } ?: emptyList()

                    Log.i("Repository", "Fetched product list: $productList")
                    Result.success(productList)
                } else {
                    Log.e("Repository", "Campus man not found for hostel: $hostelNumber")
                    Result.failure(Exception("Campus man not found for the specified hostel"))
                }
            } else {
                Log.e("Repository", "No admin found for the given college: $college")
                Result.failure(Exception("Admin not found for the specified college"))
            }
        } catch (e: Exception) {
            Log.e("Repository", "Failed to fetch product list: ${e.message}")
            Result.failure(e)
        }
    }



    override suspend fun getCollageList(): Result<List<String>> {
        return try {
            val snapshot = firestore.collection("CollegeList").get().await()
            val collageNames = snapshot.documents.mapNotNull { it.getString("college") }
            Result.success(collageNames)
        } catch (e: Exception) {
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

    override suspend fun logout(): Result<Boolean> {
        return try {
            auth.signOut()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}