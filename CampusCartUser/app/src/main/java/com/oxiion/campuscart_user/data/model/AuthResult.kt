package com.oxiion.campuscart_user.data.model

data class AuthResult(
    val uid: String="",
    val products:List<Product> = listOf()
)
