package com.oxiion.campuscart_user.domain.repository

import com.oxiion.campuscart_user.data.model.AuthResult
import com.oxiion.campuscart_user.data.model.Product
import com.oxiion.campuscart_user.data.model.User

interface AuthRepository {
    suspend fun signin(email: String, password: String): Result<AuthResult>
    suspend fun signup(user: User, password: String): Result<AuthResult>
    suspend fun logout(): Result<Boolean>
    suspend fun getCollageList(): Result<List<String>>
    suspend fun getHostelList(college: String): Result<List<String>>
    suspend fun getUserData(uid: String): User?
    suspend fun fetchProductList(college: String, hostelNumber: String): Result<List<Product>>
}