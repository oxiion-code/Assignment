package com.oxiion.campuscart.data.models.productUtils

import com.google.firebase.Timestamp
import com.oxiion.campuscart.data.models.roles.CampusMan
import com.oxiion.campuscart.data.models.roles.User
import kotlinx.serialization.Serializable


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

