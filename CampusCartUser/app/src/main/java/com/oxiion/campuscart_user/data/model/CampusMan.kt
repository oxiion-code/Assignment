package com.oxiion.campuscart_user.data.model




data class CampusMan(
    var id: String="",
    var imageUrl:String="",
    var college: String="",
    val address: Address = Address(),
    val memberStockItems:List<Product> =listOf(),
    val orders: List<Order> = listOf(),
    val pastOrders:List<Order> = listOf(),
    val earningList:List<Earning> = listOf()
)
