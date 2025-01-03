package com.oxiion.campuscart_user.domain.repository

interface AuthRepository {
    fun signin(username: String, password: String): Result<String>
    fun signup(username: String, password: String, email: String): Result<String>
}