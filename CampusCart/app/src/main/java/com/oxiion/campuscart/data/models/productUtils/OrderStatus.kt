package com.oxiion.campuscart.data.models.productUtils

import kotlinx.serialization.Serializable


data class OrderStatus(
    val isDelivered: Boolean=false,
    val isCancelled: Boolean=false,
    val isOnProgress:Boolean=true,
)
