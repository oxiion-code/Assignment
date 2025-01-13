package com.oxiion.campuscart_user.data.model


data class Order(
    val id: String,
    val confirmationCode:String,
    val timestamp: Long,
    val items: List<Product>,
    val quantity: Int,
    val totalPrice: Double,
    val status: OrderStatus,
    val receipt: String,
    val address: Address=Address()
)

