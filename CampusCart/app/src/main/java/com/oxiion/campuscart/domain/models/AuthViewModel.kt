package com.oxiion.campuscart.domain.models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.data.models.roles.Admin
import com.oxiion.campuscart.data.models.roles.CampusMan
import com.oxiion.campuscart.domain.repository.AdminRepository
import com.oxiion.campuscart.utils.LoginState
import com.oxiion.campuscart.utils.SharedPreferencesManager
import com.oxiion.campuscart.utils.StateData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AdminRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _loginState = MutableStateFlow<StateData>(StateData.Idle)
    val loginState: StateFlow<StateData> get() = _loginState
    private val _signUpState = MutableStateFlow<LoginState>(LoginState.Idle)
    val signUpState: StateFlow<LoginState> = _signUpState
    private val _signOutState = MutableStateFlow<LoginState>(LoginState.Idle)
    val signOutState: StateFlow<LoginState> = _signOutState
    private val _adminData = MutableStateFlow<Admin?>(null)
    val adminData: StateFlow<Admin?> = _adminData

    var uid: String? = ""

    init {
        // Load UID from SharedPreferences when the ViewModel is created
        uid = SharedPreferencesManager.getUid(context)
        if (uid != null) {
            fetchAdminData(uid)
        }
    }

    fun signIn(admin: Admin, password: MutableState<String>) {
        viewModelScope.launch {
            _signUpState.value = LoginState.Loading
            val result = repository.signIn(admin, password.value)
            if (result.isSuccess) {
                _signUpState.value = LoginState.Success
            } else {
                _signUpState.value =
                    LoginState.Error(result.exceptionOrNull()?.localizedMessage ?: "Unknown Error")
            }
        }
    }

    fun login(userEmail: String, password: String) {
        viewModelScope.launch {
            _signOutState.value = LoginState.Idle
            _loginState.value = StateData.Loading
            val result = repository.login(userEmail, password)
            if (result.isSuccess) {
                _loginState.value = StateData.Success
                uid = result.getOrNull()
                if (uid != null) {
                    SharedPreferencesManager.saveUid(context, uid!!) // Save UID to SharedPreferences
                    fetchAdminData(uid)
                }
            } else {
                _loginState.value = StateData.Error(
                    result.exceptionOrNull()?.message ?: "Unknown Error"
                )
            }
        }
    }

    fun fetchAdminData(uid: String?) {
        viewModelScope.launch {
            val result = uid?.let { repository.fetchAdminData(it) }
            if (result != null) {
                if (result.isSuccess) {
                    _adminData.value = result.getOrNull()
                    Log.i(
                        "Fetch Admin Data",
                        "Admin Data Fetched Successfully:${adminData.value?.name}"
                    )
                } else {
                    Log.i("Fetch Admin Data", "Error in fetchAdminData")
                }
            }
        }
    }

    fun refreshAdminData() {
        viewModelScope.launch {
            // Re-fetch the data and update the state
            fetchAdminData(uid)
        }
    }


    fun logout() {
        viewModelScope.launch {
            _signOutState.value = LoginState.Loading
            val result = repository.logout()
            if (result.isSuccess) {
                _signOutState.value = LoginState.Success
                SharedPreferencesManager.removeUid(context)
                resetState() // Reset the ViewModel state on logout
            } else {
                _signOutState.value = LoginState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown Error"
                )
            }
        }
    }

    private fun resetState() {
        _loginState.value = StateData.Idle
        _signUpState.value = LoginState.Idle
        _adminData.value = null
        uid = null
    }

    fun resetLoginState() {
        _loginState.value = StateData.Idle // Replace with the appropriate initial state
    }
    fun getProductById(productId: String?): Product? {
        return adminData.value?.stockItems?.find { it.id == productId }
    }
    fun getCampusManById(campusmanId:String): CampusMan? {
        return adminData.value?.employeeList?.find { it.id==campusmanId }
    }
}
