package com.oxiion.campuscart.domain.repository

import com.oxiion.campuscart.data.models.productUtils.Order
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.data.models.roles.CampusMan

interface CampusManRepository {
    suspend fun addMember(campusMan: CampusMan):Result<Boolean>
    suspend fun updateMember(campusMan: CampusMan):Result<Boolean>
    suspend fun deleteMember(campusManId: String):Result<Boolean>
    suspend fun addStockItemToCampusMan(campusManId: String, product: Product): Result<Boolean>
    suspend fun getCampusManById(campusManId: String): CampusMan?
    suspend fun getLiveOrders(adminId: String, campusManId: String): List<Order>
    suspend fun getPastOrders(adminId: String, campusManId: String): List<Order>
}
