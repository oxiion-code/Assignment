package com.oxiion.campuscart_user.data.model

import kotlinx.serialization.Serializable


data class OrderStatus(
    val isDelivered: Boolean=false,
    val isCancelled: Boolean=false,
    val isOnProgress:Boolean=true,
)
