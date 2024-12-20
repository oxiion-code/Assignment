package com.oxiion.campuscart.data.models.productUtils

import com.google.firebase.Timestamp
import com.oxiion.campuscart.data.models.roles.CampusMan
import com.oxiion.campuscart.data.models.roles.User

data class Order(
    val user: User,
    val campusMan: CampusMan,
    val id: String,
    val timestamp: Timestamp,
    val item: Product,
    val quantity: Int,
    val totalPrice: Double,
    val status: OrderStatus,
    val receipt:String
)
