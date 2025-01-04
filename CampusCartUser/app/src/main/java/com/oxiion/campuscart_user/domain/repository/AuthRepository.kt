package com.oxiion.campuscart_user.domain.repository

import com.oxiion.campuscart_user.data.model.User

interface AuthRepository {
   suspend fun signin(email: String, password: String): Result<String>
   suspend fun signup(user: User, password: String): Result<String>
   suspend fun getCollageList(): Result<List<String>>
   suspend fun getHostelList(college:String): Result<List<String>>
}