package com.oxiion.campuscart_user.data.model

import com.google.firebase.Timestamp
import kotlinx.serialization.Serializable


data class Payment(
    var month: String,
    var amount: String,
    var id: String,
    var recipt: String,
    var timestamp: Long
)
