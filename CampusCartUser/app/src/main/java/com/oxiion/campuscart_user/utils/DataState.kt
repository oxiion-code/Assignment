package com.oxiion.campuscart_user.utils

import com.oxiion.campuscart_user.data.model.Product

sealed class DataState{
    data object Idle : DataState()
    data object Loading : DataState()
    data object Success : DataState()
    data class Error(val message: String) : DataState()
}
sealed class DataStateAuth{
    data object Idle : DataStateAuth()
    data object Loading : DataStateAuth()
    data class Success(val product:List<Product>) : DataStateAuth()
    data class Error(val message: String) : DataStateAuth()
}