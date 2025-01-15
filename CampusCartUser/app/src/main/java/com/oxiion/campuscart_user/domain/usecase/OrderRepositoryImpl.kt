package com.oxiion.campuscart_user.domain.usecase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oxiion.campuscart_user.data.model.Address
import com.oxiion.campuscart_user.data.model.Order
import com.oxiion.campuscart_user.domain.repository.OrderRepository
import com.oxiion.campuscart_user.utils.SharedPreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : OrderRepository {

    override suspend fun createOrder(order: Order): Result<Address> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val collegeName = SharedPreferencesManager.getCollege(context)
                ?: return Result.failure(Exception("College name not found"))
            val hostelNumber = SharedPreferencesManager.getHostelNumber(context)
                ?: return Result.failure(Exception("Hostel number not found"))

            Log.e("createOrder", "Starting order creation for userId: $userId, college: $collegeName, hostel: $hostelNumber")

            val adminQuerySnapshot = firestore.collection("admins")
                .whereEqualTo("collagename", collegeName)
                .get()
                .await()

            if (adminQuerySnapshot.isEmpty) {
                Log.e("createOrder", "No admin found for the college: $collegeName")
                return Result.failure(Exception("Admin not found for the college"))
            }

            var employeeAddress: Address? = null

            firestore.runTransaction { transaction ->
                // Iterate over each admin document
                for (adminDoc in adminQuerySnapshot.documents) {
                    val employeeList = adminDoc.get("employeeList") as? List<Map<String, Any>> ?: emptyList()
                    Log.e("createOrder", "Processing admin doc: ${adminDoc.id}, employeeList size: ${employeeList.size}")

                    val updatedEmployeeList = employeeList.map { employee ->
                        val addressMap = employee["address"] as? Map<String, Any>
                        if (addressMap != null && addressMap["hostelNumber"] == hostelNumber) {
                            Log.e("createOrder", "Matching employee found with hostelNumber: $hostelNumber")

                            employeeAddress = Address(
                                fullName = addressMap["fullName"] as? String ?: "",
                                phoneNumber = addressMap["phoneNumber"] as? String ?: "",
                                hostelNumber = addressMap["hostelNumber"] as? String ?: "",
                                roomNumber = addressMap["roomNumber"] as? String ?: "",
                                rollNumber = addressMap["rollNumber"] as? String ?: "",
                                email = addressMap["email"] as? String ?: ""
                            )

                            // Add the order to the employee's orders list
                            val employeeOrders = (employee["orders"] as? List<Map<String, Any>> ?: emptyList()).toMutableList()
                            employeeOrders.add(
                                mapOf(
                                    "id" to order.id,
                                    "timestamp" to order.timestamp,
                                    "confirmationCode" to order.confirmationCode,
                                    "items" to order.items.map { it },
                                    "quantity" to order.quantity,
                                    "totalPrice" to order.totalPrice,
                                    "status" to order.status,
                                    "receipt" to order.receipt
                                )
                            )

                            Log.e("createOrder", "Order added to employee orders list: ${employeeOrders.size}")

                            // Return a modified employee map with the updated orders
                            employee.toMutableMap().apply {
                                put("orders", employeeOrders)
                            }
                        } else {
                            employee
                        }
                    }

                    // Update employee list in Firestore
                    transaction.update(adminDoc.reference, "employeeList", updatedEmployeeList)

                    // If employee address is found, no need to continue with further iterations
                    if (employeeAddress != null) break
                }

                // Update the user's order with the employee address if found
                val updatedOrder = order.copy(address = employeeAddress ?: order.address)
                val userOrdersDoc = firestore.collection("Users").document(userId).collection("orders").document(order.id)
                transaction.set(userOrdersDoc, updatedOrder)
                Log.e("createOrder", "Order added to user's orders collection")
            }.await()

            employeeAddress?.let {
                Log.e("createOrder", "Employee address found and returned: $it")
                Result.success(it)
            } ?: Result.failure(Exception("Employee address not found"))
        } catch (e: Exception) {
            Log.e("createOrder", "Error creating order: ${e.message}", e)
            Result.failure(e)
        }
    }




    override suspend fun generateOTP(orderId: String): Result<String> {
        val otp = (1..6).map { (0..9).random() }.joinToString("")
        return try {
            firestore.collection("Otp").document(otp).set(mapOf("otp" to otp, "orderId" to orderId)).await()
            Result.success(otp)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getOrders(): Result<List<Order>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            // Fetch the orders for the current user
            val ordersQuerySnapshot = firestore.collection("Users")
                .document(userId)
                .collection("orders")
                .get()
                .await()

            // Check if the query is empty
            if (ordersQuerySnapshot.isEmpty) {
                return Result.success(emptyList()) // Return empty list if no orders
            }

            // Map the documents to Order objects
            val orders = ordersQuerySnapshot.documents.mapNotNull { document ->
                val order = document.toObject(Order::class.java)
                order // Return the order object directly
            }

            Result.success(orders) // Return orders list
        } catch (e: Exception) {
            Log.e("getOrders", "Error fetching orders: ${e.message}", e)
            Result.failure(e) // Return failure if exception occurs
        }
    }

    override suspend fun cancelOrder(order: Order): Result<Boolean> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            Log.d("cancelOrder", "User ID: $userId, Order ID: ${order.id}")
            val college=SharedPreferencesManager.getCollege(context)

            // Fetch the admin query snapshot outside the transaction
            val adminQuerySnapshot = firestore.collection("admins")
                .whereEqualTo("collagename",college) // Use collegeName from the order
                .get()
                .await()
            Log.d("cancelOrder", "Admin query snapshot size: ${adminQuerySnapshot.size()}")

            firestore.runTransaction { transaction ->
                // Step 1: Update the user's wallet
                val userDocRef = firestore.collection("Users").document(userId)
                val userSnapshot = transaction.get(userDocRef)
                val currentWalletMoney = userSnapshot.getDouble("walletMoney") ?: 0.0
                val updatedWalletMoney = currentWalletMoney + order.totalPrice
                Log.d("cancelOrder", "Current wallet money: $currentWalletMoney, Updated wallet money: $updatedWalletMoney")
                transaction.update(userDocRef, "walletMoney", updatedWalletMoney)

                // Step 2: Delete the user's order
                val orderDocRef = userDocRef.collection("orders").document(order.id)
                transaction.delete(orderDocRef)
                Log.d("cancelOrder", "Deleted order ${order.id} from user's orders collection")

                // Step 3: Remove the order from the employee's orders list
                for (adminDoc in adminQuerySnapshot.documents) {
                    val employeeList = adminDoc.get("employeeList") as? List<Map<String, Any>> ?: emptyList()
                    Log.d("cancelOrder", "Admin Doc ID: ${adminDoc.id}, Employee list size: ${employeeList.size}")

                    val updatedEmployeeList = employeeList.map { employee ->
                        val addressMap = employee["address"] as? Map<String, Any>
                        if (addressMap != null && addressMap["hostelNumber"] == order.address.hostelNumber) {
                            Log.d("cancelOrder", "Matching employee found with hostelNumber: ${order.address.hostelNumber}")

                            val employeeOrders = (employee["orders"] as? List<Map<String, Any>> ?: emptyList()).toMutableList()
                            Log.d("cancelOrder", "Employee orders size before removal: ${employeeOrders.size}")

                            // Remove the matching order from the employee's orders
                            val updatedOrders = employeeOrders.filterNot { it["id"] == order.id }
                            Log.d("cancelOrder", "Employee orders size after removal: ${updatedOrders.size}")

                            // Return the updated employee map
                            employee.toMutableMap().apply {
                                put("orders", updatedOrders)
                            }
                        } else {
                            employee
                        }
                    }

                    // Update the admin document with the modified employee list
                    transaction.update(adminDoc.reference, "employeeList", updatedEmployeeList)
                    Log.d("cancelOrder", "Updated employee list for admin doc: ${adminDoc.id}")
                }
            }.await()

            // Step 4: Delete OTP document
            val otpQuerySnapshot = firestore.collection("Otp")
                .whereEqualTo("otp", order.confirmationCode)
                .whereEqualTo("orderId", order.id)
                .get()
                .await()
            Log.d("cancelOrder", "OTP query snapshot size: ${otpQuerySnapshot.size()}")

            for (otpDoc in otpQuerySnapshot.documents) {
                otpDoc.reference.delete().await()
                Log.d("cancelOrder", "Deleted OTP document: ${otpDoc.id}")
            }

            Log.d("cancelOrder", "Order cancelled successfully, wallet money updated, and employee orders updated")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("cancelOrder", "Error cancelling order: ${e.message}", e)
            Result.failure(e)
        }
    }





    override suspend fun deductWalletMoneyForPayment(amountToPay: Double): Result<Double> {
        if (amountToPay <= 0.0) {
            return Result.failure(Exception("Amount to pay must be greater than 0"))
        }
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            var remainingToPay = 0.0 // Initialize outside the transaction scope

            firestore.runTransaction { transaction ->
                val userDocRef = firestore.collection("Users").document(userId)
                val userSnapshot = transaction.get(userDocRef)

                val currentWalletMoney = userSnapshot.getDouble("walletMoney") ?: 0.0
                val walletDeduction = if (currentWalletMoney >= amountToPay) amountToPay else currentWalletMoney
                remainingToPay = amountToPay - walletDeduction

                val updatedWalletMoney = currentWalletMoney - walletDeduction
                transaction.update(userDocRef, "walletMoney", updatedWalletMoney)
            }.await()

            Result.success(remainingToPay)
        } catch (e: Exception) {
            Log.e("deductWalletMoneyForPayment", "Error during wallet deduction: ${e.message}", e)
            Result.failure(e)
        }
    }


}



