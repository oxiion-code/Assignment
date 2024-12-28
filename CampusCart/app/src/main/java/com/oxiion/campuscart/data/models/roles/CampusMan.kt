package com.oxiion.campuscart.data.models.roles


import com.oxiion.campuscart.data.models.productUtils.Address
import com.oxiion.campuscart.data.models.productUtils.Order
import com.oxiion.campuscart.data.models.productUtils.Product


data class CampusMan(
    var id: String="",
    var imageUrl:String="",
    val address: Address=Address(),
    val memberStockItems:List<Product> =listOf(),
    val orders: List<Order> = listOf(),
    val pastOrders:List<Order> = listOf(),
)
