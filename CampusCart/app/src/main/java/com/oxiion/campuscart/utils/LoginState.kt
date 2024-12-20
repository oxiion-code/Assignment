package com.oxiion.campuscart.utils

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
sealed class LoginStateAdminData{
    data object Idle : LoginStateAdminData()
    data object Loading : LoginStateAdminData()
    data object Success : LoginStateAdminData()
    data class Error(val message: String) : LoginStateAdminData()
}