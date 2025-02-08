package com.oxiion.campuscart_user.domain.repository

import com.oxiion.campuscart_user.data.datasource.local.CartItem
import com.oxiion.campuscart_user.data.model.Address
import com.oxiion.campuscart_user.data.model.Order


interface OrderRepository{
    suspend fun createOrder(order: Order):Result<Address>
    suspend fun getOrders(): Result<List<Order>>
    suspend fun getOrderById(orderId:String):Result<Order>
    suspend fun cancelOrder(order: Order):Result<Boolean>
    suspend fun generateOTP(orderId:String): Result<String>
    suspend fun deductWalletMoneyForPayment(amountToPay: Double): Result<Double>
    suspend fun isOrderAvailable(
        cartItems: List<CartItem>,
    ): Result<Boolean>
}