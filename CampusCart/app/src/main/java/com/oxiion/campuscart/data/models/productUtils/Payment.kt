package com.oxiion.campuscart.data.models.productUtils

import com.google.firebase.Timestamp

data class Payment(
    var month: String,
    var amount: String,
    var id: String,
    var recipt: String,
    var timestamp: Timestamp
)
