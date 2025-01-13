package com.oxiion.campuscart_user.data.model


data class User(
    val uid: String="",
    var college:String="",
    var profilePicture: String? = null,
    var address: Address? = null,
    var cart: List<Product> = listOf(),
    var orders: List<Order> = listOf(),
    var walletMoney:Double=0.0
)
