package com.oxiion.campuscart_user.data.model


data class Order(
    val user: User,
    val campusManId: Int, // Replace CampusMan object with ID
    val id: String,
    val timestamp: Long,
    val items: List<Product>,
    val quantity: Int,
    val totalPrice: Double,
    val status: OrderStatus,
    val receipt: String
)

