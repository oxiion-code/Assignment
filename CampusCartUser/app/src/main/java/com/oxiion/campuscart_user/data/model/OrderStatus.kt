package com.oxiion.campuscart_user.data.model

import kotlinx.serialization.SerialName


data class OrderStatus(
    @SerialName("delivered") val isDelivered: Boolean = false,
    @SerialName("cancelled") val isCancelled: Boolean = false,
    @SerialName("onProgress") val isOnProgress: Boolean = true
)

