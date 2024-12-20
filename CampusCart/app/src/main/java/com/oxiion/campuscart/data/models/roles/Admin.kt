package com.oxiion.campuscart.data.models.roles

import com.oxiion.campuscart.data.models.productUtils.Product

data class Admin(
    val name: String="Rudra Narayan Rath",
    val securityCode: String="",
    var email:String="",
    var role: String = "admin",
    var stockItems:List<Product> = listOf(),
    var collageList: List<String> = listOf("Gita Autonomous College"),
    var employeeList:List<CampusMan> = listOf()
)
