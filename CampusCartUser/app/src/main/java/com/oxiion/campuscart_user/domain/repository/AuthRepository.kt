package com.oxiion.campuscart_user.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.oxiion.campuscart_user.data.model.AuthResult
import com.oxiion.campuscart_user.data.model.Product
import com.oxiion.campuscart_user.data.model.User

interface AuthRepository {

    suspend fun signup(user: User, password: String): Result<AuthResult>
    suspend fun signin(email: String, password: String): Result<AuthResult>
    suspend fun getUserData(uid: String): User?
    suspend fun fetchProductList(college: String, hostelNumber: String): Result<List<Product>>
    suspend fun getCollageList(): Result<List<String>>
    suspend fun getHostelList(college: String): Result<List<String>>
    suspend fun logout(): Result<Boolean>
    suspend fun updateUserDetails(
        name: String,
        phoneNumber: String,
        hostelNumber: String
    ): Result<Boolean>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Boolean>
    suspend fun getCurrentUser(): FirebaseUser?
    suspend fun verifyEmail(): Result<String>
    suspend fun forgotPassword(email: String): Result<Boolean>
    suspend fun deleteAccount(password: String): Result<Boolean>

}