package com.oxiion.campuscart_user.data.model


data class User(
    var name: String,
    var email:String,
    var college:String,
    var phoneNumber:String,
    var password:String,
    var profilePicture: String? = null,
    var address: Address? = null,
    var cart: List<Product> = listOf(),
    var orders: List<Order> = listOf()
)
