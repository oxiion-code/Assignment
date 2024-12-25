package com.oxiion.campuscart.data.models.roles

import com.oxiion.campuscart.data.models.productUtils.Address
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.data.models.productUtils.Order
import kotlinx.serialization.Serializable

data class User(
    var name: String,
    var email:String,
    var phoneNumber:String,
    var password:String,
    var profilePicture: String? = null,
    var address: Address? = null,
    var cart: List<Product> = listOf(),
    var orders: List<Order> = listOf()
)
