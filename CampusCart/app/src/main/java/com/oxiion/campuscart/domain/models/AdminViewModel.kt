package com.oxiion.campuscart.domain.models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.data.models.roles.Admin
import com.oxiion.campuscart.domain.repository.AdminRepository
import com.oxiion.campuscart.utils.LoginState
import com.oxiion.campuscart.utils.LoginStateAdminData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel@Inject constructor(
    private val repository: AdminRepository
):ViewModel() {
    private val _loginState= MutableStateFlow<LoginStateAdminData>(LoginStateAdminData.Idle)
    val loginState:StateFlow<LoginStateAdminData> get() = _loginState
    private val _signUpState=MutableStateFlow<LoginState>(LoginState.Idle)
    val signUpState:StateFlow<LoginState> =_signUpState
    private val _signOutState=MutableStateFlow<LoginState>(LoginState.Idle)
    val signOutState:StateFlow<LoginState> =_signOutState
    private val _adminData=MutableStateFlow<Admin?>(null)
    val adminData:StateFlow<Admin?> =_adminData
    private val _addProductState=MutableStateFlow<LoginState>(LoginState.Idle)
    val addProductState:StateFlow<LoginState> =_addProductState

    private var uid:String?=""

    fun signIn(admin:Admin,password: MutableState<String>){
        viewModelScope.launch {
            _signUpState.value=LoginState.Loading
            val result=repository.signIn(admin,password.value)
            if (result.isSuccess){
                _signUpState.value=LoginState.Success
            }else{
               _signUpState.value=LoginState.Error(result.exceptionOrNull()?.localizedMessage?:"Unknown Error")
            }
        }
    }
    fun login(userEmail: String,password: String){
        viewModelScope.launch {
            _loginState.value=LoginStateAdminData.Loading
            val result=repository.login(userEmail,password)
            if (result.isSuccess){
                _loginState.value=LoginStateAdminData.Success
                uid=result.getOrNull()
                fetchAdminData(uid)
            }else {
                _loginState.value = LoginStateAdminData.Error(
                    result.exceptionOrNull()?.message ?: "Unknown Error"
                )
            }
        }
    }
    fun fetchAdminData(uid:String?){
        viewModelScope.launch {
            val result= uid?.let { repository.fetchAdminData(it) }
            if (result != null) {
                if (result.isSuccess){
                    _adminData.value=result.getOrNull()
                    Log.i("Fetch Admin Data","Admin Data Fetched Successfully:${adminData.value?.name}")
                }else{
                    Log.i("Fetch Admin Data","Error in fetchAdminData")
                }
            }
        }
    }
    fun addProduct(product: Product){
        viewModelScope.launch {
            val result=repository.addProduct(product)
            _addProductState.value=LoginState.Loading
            if (result.isSuccess){
                _addProductState.value=LoginState.Success
               Log.i("ProductAdd","Product added to the admin List")
            }else {
                _addProductState.value=LoginState.Error(result.exceptionOrNull()?.localizedMessage?:"Unknown Error")
                Log.i("Productadd", "Failed to add product")
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
           _signOutState.value=LoginState.Loading
            val result=repository.logout()
            if (result.isSuccess){
               _signOutState.value=LoginState.Success
            }else{
                LoginState.Error(result.exceptionOrNull()?.message.toString()?:"Unknown Error")
                    .also { _signOutState.value = it }
            }
        }
    }
}