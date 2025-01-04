package com.oxiion.campuscart_user.data.model

import kotlinx.serialization.Serializable

data class Address(
    var fullName: String="",
    var phoneNumber: String="",
    var hostelNumber:String="",
    var roomNumber:String="",
    var rollNumber:String="",
    var email:String=""
)
