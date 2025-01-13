package com.oxiion.campuscart_user.viewmodels

import android.content.Context
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var otp: String? = null
    private val _orderData = MutableStateFlow<Order?>(null)
    val orderData: MutableStateFlow<Order?> = _orderData

    private val _ordersList = MutableStateFlow<List<Order>?>(null)
    val ordersList: MutableStateFlow<List<Order>?> = _ordersList

    private val _campusManAddress = MutableStateFlow<Address?>(null)
    val campusManAddress: MutableStateFlow<Address?> = _campusManAddress

    private val _orderCreationState = MutableStateFlow<DataState>(DataState.Idle)
    val orderCreationState: MutableStateFlow<DataState> = _orderCreationState

    private val _getOrdersState= MutableStateFlow<DataState>(DataState.Idle)
    val getOrdersState: MutableStateFlow<DataState> = _getOrdersState

    fun createOrder(products: List<CartItem>, totalPrice: Double, receipt: String) {
        viewModelScope.launch {
            _orderCreationState.value = DataState.Loading
            // Generate the order ID
            val orderId = "OD-" + System.currentTimeMillis().toString()

            // Generate OTP
            val resultOtp = repository.generateOTP(orderId)
            if (resultOtp.isSuccess) {
                otp = resultOtp.getOrNull()
            }

            if (otp != null) {
                // Create the order object
                val order = Order(
                    id = orderId,
                    timestamp = System.currentTimeMillis(),
                    confirmationCode = otp!!,
                    items = products.map {
                        Product(
                            id = it.productId,
                            name = it.productName,
                            category = it.productCategory,
                            quantity = it.quantity,
                            rating = it.rating,
                            isAvailable = it.isAvailable,
                            discount = it.discountedPrice,
                            description = it.description,
                            price = it.price,
                            image = it.image
                        )
                    },
                    quantity = products.sumOf { it.quantity },
                    totalPrice = totalPrice,
                    status = OrderStatus(),
                    receipt = receipt
                )

                // Save the order and fetch employee address
                val result = repository.createOrder(order)
                if (result.isSuccess) {
                    _orderData.value = order
                    _campusManAddress.value = result.getOrNull()
                    _orderCreationState.value = DataState.Success
                } else {
                    _orderCreationState.value = DataState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
                }
            }
        }
    }

    fun resetOrderCreationState() {
        _orderCreationState.value = DataState.Idle
    }

    fun getOrders() {
        viewModelScope.launch {
            _getOrdersState.value = DataState.Loading
            val result = repository.getOrders()
            if (result.isSuccess){
                _getOrdersState.value = DataState.Success
                _ordersList.value = result.getOrNull()
            }else{
                _getOrdersState.value = DataState.Error(result.exceptionOrNull()?.message?: "Unknown Error")
            }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
        }
    }
}

