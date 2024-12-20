package com.oxiion.campuscart.domain.repository

import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.data.models.roles.Admin

interface AdminRepository {
    suspend fun signIn(admin: Admin,password: String):Result<Boolean>
    suspend fun login(userEmail: String, password: String): Result<String>
    suspend fun fetchAdminData(uid:String):Result<Admin>
    suspend fun addProduct(product: Product):Result<Boolean>
    suspend fun logout(): Result<Boolean>
}