package com.oxiion.campuscart.data.models.roles

import com.oxiion.campuscart.data.models.productUtils.Address
import com.oxiion.campuscart.data.models.productUtils.Product

data class CampusMan(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val address: Address,
    val delivery: List<Product> = listOf()
)
