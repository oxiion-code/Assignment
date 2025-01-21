package com.oxiion.campuscart_user.data.model

import kotlinx.serialization.SerialName


data class OrderStatus(
     val delivered: Boolean = false,
     val cancelled: Boolean = false,
     val onProgress: Boolean = true
)

