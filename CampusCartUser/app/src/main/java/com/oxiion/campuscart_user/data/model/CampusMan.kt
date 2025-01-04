package com.oxiion.campuscart.data.models.roles


import com.oxiion.campuscart_user.data.model.Address
import com.oxiion.campuscart_user.data.model.Order
import com.oxiion.campuscart_user.data.model.Product


data class CampusMan(
    var id: String="",
    var imageUrl:String="",
    var college: String="",
    val address: Address = Address(),
    val memberStockItems:List<Product> =listOf(),
    val orders: List<Order> = listOf(),
    val pastOrders:List<Order> = listOf(),
)
