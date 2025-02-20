package com.meow.movieflex

sealed class DataState{
    data object Idle : DataState()
    data object Loading : DataState()
    data class Success<T>(val data: T) : DataState()
    data class Error(val message: String) : DataState()
}