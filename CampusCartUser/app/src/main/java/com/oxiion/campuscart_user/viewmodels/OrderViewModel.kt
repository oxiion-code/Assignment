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

    private val _getOrdersState = MutableStateFlow<DataState>(DataState.Idle)
    val getOrdersState: StateFlow<DataState> = _getOrdersState

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

    fun cancelOrder(order: Order) {
        viewModelScope.launch {
            _cancelOrderState.value=DataState.Idle
            val result = repository.cancelOrder(order)
            if (result.isSuccess){
                _cancelOrderState.value=DataState.Success
                Log.d("OrderViewModel","Order cancelled successfully")
            }else{
                _cancelOrderState.value=DataState.Error(result.exceptionOrNull()?.message.toString())
            }
            // Implement cancel logic here
        }
    }

    private fun CartItem.toProduct(): Product {
        return Product(
            id = productId,
            name = productName,
            category = productCategory,
            quantity = quantity,
            rating = rating,
            isAvailable = isAvailable,
            discount = discountedPrice,
            description = description,
            price = price,
            image = image
        )
    }
}
