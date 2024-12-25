package com.oxiion.campuscart.data.models.roles

import android.os.Parcel
import android.os.Parcelable
import com.oxiion.campuscart.data.models.productUtils.Address
import com.oxiion.campuscart.data.models.productUtils.Order
import com.oxiion.campuscart.data.models.productUtils.Product
import kotlinx.serialization.Serializable

data class CampusMan(
    var id: String="",
    var imageUrl:String="",
    val address: Address=Address(),
    val orders: List<Order> = listOf()
)
