package com.oxiion.campuscart.utils

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
sealed class StateData{
    data object Idle : StateData()
    data object Loading : StateData()
    data object Success : StateData()
    data class Error(val message: String) : StateData()
}