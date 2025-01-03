package com.oxiion.campuscart_user.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oxiion.campuscart_user.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
):AuthRepository {
    override fun signin(username: String, password: String): Result<String> {
        TODO("Not yet implemented")
    }
    override fun signup(username: String, password: String, email: String): Result<String> {
        TODO("Not yet implemented")
    }
}