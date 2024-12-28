package com.oxiion.campuscart.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.random.Random

@SuppressLint("SimpleDateFormat")
fun generateSecureOrderId(): String {
    // Prefix for the order
    val prefix = "ORD"

    // Current date in YYMMDD format
    val dateFormat = SimpleDateFormat("yyMMdd")
    val currentDate = dateFormat.format(Date())

    // Generate a random 4-digit alphanumeric string
    val alphanumeric = ('A'..'Z') + ('0'..'9')
    val randomSuffix = (1..4).map { alphanumeric.random() }.joinToString("")

    // Combine to form the order ID
    return "$prefix-$currentDate-$randomSuffix"
}

