package com.oxiion.campuscart_user.domain.usecase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oxiion.campuscart_user.data.datasource.local.CartItem
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
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val collegeName = SharedPreferencesManager.getCollege(context)
                ?: return Result.failure(Exception("College name not found"))
            val hostelNumber = SharedPreferencesManager.getHostelNumber(context)
                ?: return Result.failure(Exception("Hostel number not found"))

            val adminQuerySnapshot = firestore.collection("admins")
                .whereEqualTo("collagename", collegeName)
                .get()
                .await()

            if (adminQuerySnapshot.isEmpty) {
                return Result.failure(Exception("Admin not found for the college"))
            }

            var employeeAddress: Address? = null

            firestore.runTransaction { transaction ->
                for (adminDoc in adminQuerySnapshot.documents) {
                    val employeeList =
                        adminDoc.get("employeeList") as? List<Map<String, Any>> ?: emptyList()
                    val updatedEmployeeList = employeeList.map { employee ->
                        val addressMap = employee["address"] as? Map<String, Any>
                        if (addressMap != null && addressMap["hostelNumber"] == hostelNumber) {
                            employeeAddress = Address(
                                fullName = addressMap["fullName"] as? String ?: "",
                                phoneNumber = addressMap["phoneNumber"] as? String ?: "",
                                hostelNumber = addressMap["hostelNumber"] as? String ?: "",
                                roomNumber = addressMap["roomNumber"] as? String ?: "",
                                rollNumber = addressMap["rollNumber"] as? String ?: "",
                                email = addressMap["email"] as? String ?: ""
                            )

                            val employeeOrders =
                                (employee["orders"] as? MutableList<Map<String, Any>>)?.toMutableList()
                                    ?: mutableListOf()

                            val memberStockItems =
                                (employee["memberStockItems"] as? MutableList<Map<String, Any>>)?.toMutableList()
                                    ?: mutableListOf()

                            val stockMap =
                                memberStockItems.associateBy { it["id"] as String }.toMutableMap()
                            for (orderedProduct in order.items) {
                                val stockItem = stockMap[orderedProduct.id]?.toMutableMap()
                                if (stockItem != null) {
                                    val currentQuantity =
                                        (stockItem["quantity"] as? Long)?.toInt() ?: 0
                                    val newQuantity =
                                        maxOf(0, currentQuantity - orderedProduct.quantity)
                                    stockItem["quantity"] = newQuantity
                                    stockMap[orderedProduct.id] = stockItem
                                }
                            }

                            val updatedStockList = stockMap.values.toList()
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

                            employee.toMutableMap().apply {
                                put("orders", employeeOrders)
                                put("memberStockItems", updatedStockList)
                            }
                        } else {
                            employee
                        }
                    }

                    transaction.update(adminDoc.reference, "employeeList", updatedEmployeeList)

                    if (employeeAddress != null) break
                }

                val updatedOrder = order.copy(address = employeeAddress ?: order.address)
                val userOrdersDoc =
                    firestore.collection("Users").document(userId).collection("orders")
                        .document(order.id)
                transaction.set(userOrdersDoc, updatedOrder)
            }.await()

            employeeAddress?.let { Result.success(it) }
                ?: Result.failure(Exception("Employee address not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun generateOTP(orderId: String): Result<String> {
        val otp = (1..6).map { (0..9).random() }.joinToString("")
        return try {
            firestore.collection("Otp").document(otp).set(mapOf("otp" to otp, "orderId" to orderId))
                .await()
            Result.success(otp)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrders(): Result<List<Order>> {
        return try {
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

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

    override suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val orderDocRef = firestore.collection("Users")
                .document(userId)
                .collection("orders")
                .document(orderId)
                .get()
                .await()

            if (!orderDocRef.exists()) {
                return Result.failure(Exception("Order not found"))
            }

            val order = orderDocRef.toObject(Order::class.java)
                ?: return Result.failure(Exception("Failed to parse Order object"))

            Result.success(order)
        } catch (e: Exception) {
            Log.e("getOrderById", "Error fetching order: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun cancelOrder(order: Order): Result<Boolean> {
        return try {
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            Log.d("cancelOrder", "User ID: $userId, Order ID: ${order.id}")
            val college = SharedPreferencesManager.getCollege(context)

            // Fetch the admin query snapshot outside the transaction
            val adminQuerySnapshot = firestore.collection("admins")
                .whereEqualTo("collagename", college)
                .get()
                .await()
            Log.d("cancelOrder", "Admin query snapshot size: ${adminQuerySnapshot.size()}")

            firestore.runTransaction { transaction ->
                // Step 1: Refund the order amount to the user's wallet
                val userDocRef = firestore.collection("Users").document(userId)
                val userSnapshot = transaction.get(userDocRef)
                val currentWalletMoney = userSnapshot.getDouble("walletMoney") ?: 0.0
                val updatedWalletMoney = currentWalletMoney + order.totalPrice
                Log.d(
                    "cancelOrder",
                    "Current wallet: $currentWalletMoney, Updated wallet: $updatedWalletMoney"
                )
                transaction.update(userDocRef, "walletMoney", updatedWalletMoney)

                // Step 2: Delete the user's order
                val orderDocRef = userDocRef.collection("orders").document(order.id)
                transaction.delete(orderDocRef)
                Log.d("cancelOrder", "Deleted order ${order.id} from user's collection")

                // Step 3: Update the employee's orders and re-add stock
                for (adminDoc in adminQuerySnapshot.documents) {
                    val employeeList =
                        adminDoc.get("employeeList") as? List<Map<String, Any>> ?: emptyList()
                    Log.d(
                        "cancelOrder",
                        "Admin Doc ID: ${adminDoc.id}, Employee list size: ${employeeList.size}"
                    )

                    val updatedEmployeeList = employeeList.map { employee ->
                        val addressMap = employee["address"] as? Map<String, Any>
                        if (addressMap != null && addressMap["hostelNumber"] == order.address.hostelNumber) {
                            Log.d(
                                "cancelOrder",
                                "Matching employee found with hostelNumber: ${order.address.hostelNumber}"
                            )

                            val employeeOrders = (employee["orders"] as? List<Map<String, Any>>
                                ?: emptyList()).toMutableList()
                            Log.d(
                                "cancelOrder",
                                "Employee orders size before removal: ${employeeOrders.size}"
                            )

                            // Remove the canceled order from the employee's order list
                            val updatedOrders = employeeOrders.filterNot { it["id"] == order.id }
                            Log.d(
                                "cancelOrder",
                                "Employee orders size after removal: ${updatedOrders.size}"
                            )

                            // ✅ Step 4: Restore product stock in `memberStockItems`
                            val memberStockItems =
                                (employee["memberStockItems"] as? List<Map<String, Any>>
                                    ?: emptyList()).toMutableList()

                            for (orderedProduct in order.items) {
                                var found = false

                                for (i in memberStockItems.indices) {
                                    val stockProduct = memberStockItems[i]
                                    if (stockProduct["id"] == orderedProduct.id) {
                                        val currentQuantity =
                                            (stockProduct["quantity"] as? Long)?.toInt() ?: 0
                                        val updatedQuantity =
                                            currentQuantity + orderedProduct.quantity

                                        // ✅ Update stock quantity
                                        memberStockItems[i] = stockProduct.toMutableMap().apply {
                                            this["quantity"] = updatedQuantity
                                        }
                                        found = true
                                        break
                                    }
                                }

                                // If the product was not found in stock, add it back
                                if (!found) {
                                    val newStockProduct = mapOf(
                                        "id" to orderedProduct.id,
                                        "name" to orderedProduct.name,
                                        "quantity" to orderedProduct.quantity
                                    )
                                    memberStockItems.add(newStockProduct)
                                }
                            }

                            Log.d(
                                "cancelOrder",
                                "Updated stock items after cancellation: $memberStockItems"
                            )

                            // Return the updated employee map
                            employee.toMutableMap().apply {
                                put("orders", updatedOrders)
                                put("memberStockItems", memberStockItems) // ✅ Update stock
                            }
                        } else {
                            employee
                        }
                    }

                    // ✅ Update Firestore with modified stock and order lists
                    transaction.update(adminDoc.reference, "employeeList", updatedEmployeeList)
                    Log.d("cancelOrder", "Updated employee list for admin doc: ${adminDoc.id}")
                }
            }.await()

            // Step 5: Delete OTP document
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

            Log.d("cancelOrder", "Order cancelled successfully, wallet refunded, and stock updated")
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
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            var remainingToPay = 0.0 // Initialize outside the transaction scope

            firestore.runTransaction { transaction ->
                val userDocRef = firestore.collection("Users").document(userId)
                val userSnapshot = transaction.get(userDocRef)

                val currentWalletMoney = userSnapshot.getDouble("walletMoney") ?: 0.0
                val walletDeduction =
                    if (currentWalletMoney >= amountToPay) amountToPay else currentWalletMoney
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

    override suspend fun isOrderAvailable(cartItems: List<CartItem>): Result<Boolean> {
        return try {
            val college = SharedPreferencesManager.getCollege(context)
            val hostel = SharedPreferencesManager.getHostelNumber(context)

            // Fetch the admin query snapshot
            val adminQuerySnapshot = firestore.collection("admins")
                .whereEqualTo("collagename", college)
                .get()
                .await()
            Log.d("isOrderAvailable", "Admin query snapshot size: ${adminQuerySnapshot.size()}")

            for (adminDoc in adminQuerySnapshot.documents) {
                val employeeList =
                    adminDoc.get("employeeList") as? List<Map<String, Any>> ?: emptyList()
                Log.d(
                    "isOrderAvailable",
                    "Admin Doc ID: ${adminDoc.id}, Employee list size: ${employeeList.size}"
                )

                for (employee in employeeList) {
                    val addressMap = employee["address"] as? Map<String, Any>
                    if (addressMap != null && addressMap["hostelNumber"] == hostel) {
                        Log.d(
                            "isOrderAvailable",
                            "Matching employee found with hostelNumber: $hostel"
                        )

                        val memberStockItems =
                            (employee["memberStockItems"] as? List<Map<String, Any>> ?: emptyList())

                        for (orderedProduct in cartItems) {
                            val stockProduct =
                                memberStockItems.find { it["id"] == orderedProduct.productId }
                            val availableQuantity =
                                (stockProduct?.get("quantity") as? Long)?.toInt() ?: 0

                            Log.d(
                                "isOrderAvailable",
                                "Checking product ID: ${orderedProduct.id}, Required: ${orderedProduct.quantity}, Available: $availableQuantity"
                            )

                            if (availableQuantity < orderedProduct.quantity) {
                                Log.d(
                                    "isOrderAvailable",
                                    "Product ${orderedProduct.id} is out of stock or insufficient quantity"
                                )
                                return Result.failure(Exception("Product ${orderedProduct.id} is out of stock or insufficient quantity"))
                            }
                        }

                        // If all items are available, return true
                        return Result.success(true)
                    }
                }
            }

            // If no matching employee is found, assume the order is not available
            Log.d(
                "isOrderAvailable",
                "No matching employee found for hostelNumber: $hostel"
            )
            Result.success(false)
        } catch (e: Exception) {
            Log.e("isOrderAvailable", "Error checking order availability: ${e.message}", e)
            Result.failure(e)
        }
    }

}



