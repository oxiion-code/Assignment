package com.oxiion.campuscart_user.data.model

import kotlinx.serialization.Serializable
data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val quantity: Int = 0,
    val rating: Double = 0.0,
    val isAvailable: Boolean = false,
    val discount: Double? = null,
    val description: String = "",
    val price: Double = 0.0,
    val image: String = ""
)
