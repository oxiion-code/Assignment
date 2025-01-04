package com.oxiion.campuscart_user.data.model

import com.oxiion.campuscart.data.models.roles.CampusMan


data class Admin(
    val name: String="Rudra Narayan Rath",
    val securityCode: String="",
    var email:String="",
    var role: String = "admin",
    var stockItems:List<Product> = listOf(),
    var collagename:String="",
    var employeeList:List<CampusMan> = listOf()
)
