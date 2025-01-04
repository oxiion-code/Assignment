package com.oxiion.campuscart_user.utils

sealed class DataState{
    data object Idle : DataState()
    data object Loading : DataState()
    data object Success : DataState()
    data class Error(val message: String) : DataState()
}