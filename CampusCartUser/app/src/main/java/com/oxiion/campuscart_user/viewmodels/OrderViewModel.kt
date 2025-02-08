package com.oxiion.campuscart_user.viewmodels

import android.content.Context
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oxiion.campuscart_user.data.datasource.local.CartItem
import com.oxiion.campuscart_user.data.model.Address
import com.oxiion.campuscart_user.data.model.Order
import com.oxiion.campuscart_user.data.model.OrderStatus
import com.oxiion.campuscart_user.data.model.Product
import com.oxiion.campuscart_user.domain.repository.OrderRepository
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.utils.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var otp: String = ""
    private val _orderData = MutableStateFlow<Order?>(null)
    val orderData: StateFlow<Order?> = _orderData

    private val _ordersList = MutableStateFlow<List<Order>?>(null)
    val ordersList: StateFlow<List<Order>?> = _ordersList

    private val _campusManAddress = MutableStateFlow<Address?>(null)
    val campusManAddress: StateFlow<Address?> = _campusManAddress

    private val _orderCreationState = MutableStateFlow<DataState>(DataState.Idle)
    val orderCreationState: StateFlow<DataState> = _orderCreationState

    private val _checkOrdersAvailabilityState= MutableStateFlow<DataState>(DataState.Idle)
    val checkOrdersAvailabilityState: StateFlow<DataState> = _checkOrdersAvailabilityState

    private val _getOrdersState = MutableStateFlow<DataState>(DataState.Idle)
    val getOrdersState: StateFlow<DataState> = _getOrdersState

    private val _getOrderState = MutableStateFlow<DataState>(DataState.Idle)
    val getOrderState: StateFlow<DataState> = _getOrderState

    private val _cancelOrderState= MutableStateFlow<DataState>(DataState.Idle)
    val cancelOrderState: StateFlow<DataState> = _cancelOrderState

    fun createOrder(products: List<CartItem>, totalPrice: Double, receipt: String) {
        viewModelScope.launch {
            _orderCreationState.value = DataState.Loading

            try {
                // Generate the order ID
                val orderId = generateOrderId()

                // Generate OTP
                val resultOtp = repository.generateOTP(orderId)
                if (resultOtp.isFailure) throw Exception("Failed to generate OTP")
                otp = resultOtp.getOrThrow()

                // Create the order object
                val order = createOrderObject(orderId, products, totalPrice, receipt, otp)

                // Save the order and deduct wallet money
                val orderResult = repository.createOrder(order)
                val walletDeductionResult = repository.deductWalletMoneyForPayment(totalPrice)

                if (orderResult.isSuccess && walletDeductionResult.isSuccess) {
                    _orderData.value = order
                    _campusManAddress.value = orderResult.getOrNull()
                    _orderCreationState.value = DataState.Success
                } else {
                    throw Exception(orderResult.exceptionOrNull()?.message ?: "Order creation failed")
                }
            } catch (e: Exception) {
                _orderCreationState.value = DataState.Error(e.message ?: "An error occurred")
            }
        }
    }

    private fun generateOrderId(): String {
        return "OD-" + System.currentTimeMillis().toString()
    }

    private fun createOrderObject(
        orderId: String,
        products: List<CartItem>,
        totalPrice: Double,
        receipt: String,
        otp: String
    ): Order {
        return Order(
            id = orderId,
            timestamp = System.currentTimeMillis(),
            confirmationCode = otp,
            items = products.map { it.toProduct() },
            quantity = products.sumOf { it.quantity },
            totalPrice = totalPrice,
            status = OrderStatus(),
            receipt = receipt
        )
    }

    fun resetOrderCreationState() {
        _orderCreationState.value = DataState.Idle
    }
    fun resetOrderCancellationState() {
        _cancelOrderState.value=DataState.Idle
    }

    fun checkOrdersAvailability(cartItems: List<CartItem>) {
        viewModelScope.launch {
            _checkOrdersAvailabilityState.value = DataState.Loading
            try {
                val result = repository.isOrderAvailable(cartItems)
                if (result.isSuccess) {
                    _checkOrdersAvailabilityState.value = DataState.Success
                } else {
                    throw Exception(result.exceptionOrNull()?.message?: "Failed to check orders availability")
                }
            } catch (e: Exception) {
                _checkOrdersAvailabilityState.value = DataState.Error(e.message?: "An error occurred")
            }
        }
    }

    fun resetCheckOrderAvailabilityState() {
        _checkOrdersAvailabilityState.value=DataState.Idle
    }
    fun fetchOrderData(orderId:String){
        _getOrderState.value=DataState.Idle
        viewModelScope.launch {
            _getOrderState.value = DataState.Loading
            try {
                val result = repository.getOrderById(orderId)
                if (result.isSuccess) {
                    _orderData.value = result.getOrNull()
                    _getOrderState.value = DataState.Success
                } else {
                    throw Exception(result.exceptionOrNull()?.message?: "Failed to fetch order data")
                }
            } catch (e: Exception) {
                _getOrderState.value = DataState.Error(e.message?: "An error occurred")
            }
        }
    }

    fun getOrders() {
        viewModelScope.launch {
            _getOrdersState.value = DataState.Loading
            try {
                val result = repository.getOrders()
                if (result.isSuccess) {
                    _ordersList.value = result.getOrNull()
                    _getOrdersState.value = DataState.Success
                } else {
                    throw Exception(result.exceptionOrNull()?.message ?: "Failed to fetch orders")
                }
            } catch (e: Exception) {
                _getOrdersState.value = DataState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            _cancelOrderState.value = DataState.Loading
            try {
                // Fetch the order by its ID
                val result = repository.getOrderById(orderId)

                if (result.isSuccess) {
                    val fetchedOrder = result.getOrNull()

                    if (fetchedOrder != null && fetchedOrder.status.onProgress) {
                        // Proceed with cancellation if the order is in progress
                        val cancelResult = repository.cancelOrder(fetchedOrder)
                        if (cancelResult.isSuccess) {
                            _cancelOrderState.value = DataState.Success
                            Log.d("OrderViewModel", "Order canceled successfully")
                        } else {
                            _cancelOrderState.value = DataState.Error(
                                cancelResult.exceptionOrNull()?.message ?: "Failed to cancel order"
                            )
                        }
                    } else {
                        // If the order is already canceled or delivered, return an error message
                        _cancelOrderState.value = DataState.Error("Order is already canceled or delivered")
                    }
                } else {
                    _cancelOrderState.value = DataState.Error(
                        result.exceptionOrNull()?.message ?: "Failed to fetch order data"
                    )
                }
            } catch (e: Exception) {
                _cancelOrderState.value = DataState.Error(e.message ?: "An error occurred")
            }
        }
    }


    private fun CartItem.toProduct(): Product {
        return Product(
            id = productId,
            name = productName,
            category = productCategory,
            quantity = quantity,
            rating = rating,
            available = isAvailable,
            discount = discountedPrice,
            description = description,
            price = price,
            image = image
        )
    }
}
