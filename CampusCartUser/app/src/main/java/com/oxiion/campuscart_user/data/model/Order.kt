package com.oxiion.campuscart_user.data.model


data class Order(
    val id: String="",
    val confirmationCode:String="",
    val timestamp: Long=0L,
    val items: List<Product> = listOf(),
    val quantity: Int=0,
    val totalPrice: Double=0.0,
    val status: OrderStatus=OrderStatus(),
    val receipt: String="",
    val address: Address=Address()
)

