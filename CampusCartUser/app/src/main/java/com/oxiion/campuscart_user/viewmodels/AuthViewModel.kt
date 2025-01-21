package com.oxiion.campuscart_user.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oxiion.campuscart_user.data.model.User
import com.oxiion.campuscart_user.data.model.Product
import com.oxiion.campuscart_user.domain.repository.AuthRepository
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.utils.DataStateAuth
import com.oxiion.campuscart_user.utils.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _getCollegeListState = MutableStateFlow<DataState>(DataState.Idle)
    val getCollegeListState: StateFlow<DataState> = _getCollegeListState

    private val _getHostelListState = MutableStateFlow<DataState>(DataState.Idle)
    val getHostelListState: StateFlow<DataState> = _getHostelListState

    private val _userData = MutableStateFlow(User())
    val userData: StateFlow<User> = _userData

    private val _signInState = MutableStateFlow<DataStateAuth>(DataStateAuth.Idle)
    val signInState: StateFlow<DataStateAuth> = _signInState

    private val _signUpState = MutableStateFlow<DataStateAuth>(DataStateAuth.Idle)
    val signUpState: StateFlow<DataStateAuth> = _signUpState

    private val _logOutState = MutableStateFlow<DataState>(DataState.Idle)
    val logOutState: StateFlow<DataState> = _logOutState

    val collegeList = MutableStateFlow<List<String>>(listOf())
    val hostelList = MutableStateFlow<List<String>>(listOf())

    private val _productList=MutableStateFlow<List<Product>>(listOf())
    val productList: StateFlow<List<Product>> = _productList

    private val _updateDetailsState = MutableStateFlow<DataState>(DataState.Idle)
    val updateDetailsState: StateFlow<DataState> = _updateDetailsState

    private val _verifyEmailState = MutableStateFlow<DataState>(DataState.Idle)
    val verifyEmailState: StateFlow<DataState> = _verifyEmailState

    private val _changePasswordState = MutableStateFlow<DataState>(DataState.Idle)
    val changePasswordState: StateFlow<DataState> = _changePasswordState

    private val _isEmailSaved= MutableStateFlow(false)
    val isEmailSaved: StateFlow<Boolean> = _isEmailSaved

    private val _sendEmailState = MutableStateFlow<DataState>(DataState.Idle)
    val sendEmailState: StateFlow<DataState> = _sendEmailState

    private val _forgotPasswordState = MutableStateFlow<DataState>(DataState.Idle)
    val forgotPasswordState: StateFlow<DataState> = _forgotPasswordState

    private val _deleteAccountState= MutableStateFlow<DataState>(DataState.Idle)
    val deleteAccountState: StateFlow<DataState> = _deleteAccountState

    private var uid: String? = null

    fun getCollegeList() {
        if (_getCollegeListState.value is DataState.Success && collegeList.value.isNotEmpty()) return

        _getCollegeListState.value = DataState.Loading
        viewModelScope.launch {
            val result = repository.getCollageList()
            if (result.isSuccess) {
                _getCollegeListState.value = DataState.Success
                collegeList.value = result.getOrNull() ?: listOf()
            } else {
                _getCollegeListState.value = DataState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun getHostelListIfNeeded(collegeName: String) {
        if (_getHostelListState.value is DataState.Success && hostelList.value.isNotEmpty()) return

        _getHostelListState.value = DataState.Loading
        viewModelScope.launch {
            val result = repository.getHostelList(collegeName)
            if (result.isSuccess) {
                _getHostelListState.value = DataState.Success
                hostelList.value = result.getOrNull() ?: listOf()
            } else {
                _getHostelListState.value = DataState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun clearHostelList() {
        hostelList.value = emptyList() // Clear current hostel list
        _getHostelListState.value = DataState.Idle // Reset state to Idle
    }

    fun saveUserDataBeforeSignUp(user: User) {
        _userData.value = user
    }

    private fun resetState() {
        _signInState.value = DataStateAuth.Idle
        _signUpState.value = DataStateAuth.Idle
        _userData.value = User()
        uid = null
    }

    // SignUp Functionality
    fun signUp(user: User, password: String) {
        _signUpState.value = DataStateAuth.Loading
        viewModelScope.launch {
            val result = repository.signup(user, password)
            result.fold(
                onSuccess = { authResult ->
                    _productList.value=authResult.products
                    // Save user UID and mark them logged in
                    SharedPreferencesManager.saveUid(context, authResult.uid)
                    SharedPreferencesManager.saveLogOutState(context, false)  // User is not logged out
                    fetchUserData(authResult.uid)
                    _signUpState.value = DataStateAuth.Success(authResult.products)
                },
                onFailure = { error ->
                    _signUpState.value = DataStateAuth.Error(error.message ?: "Signup failed")
                }
            )
        }
    }

    // SignIn Functionality
    fun signIn(email: String, password: String) {
        _signInState.value = DataStateAuth.Loading
        viewModelScope.launch {
            val result = repository.signin(email, password)
            result.fold(
                onSuccess = { authResult ->
                    _productList.value=authResult.products
                    // Save user UID and mark them logged in
                    SharedPreferencesManager.saveUid(context, authResult.uid)
                    SharedPreferencesManager.saveLogOutState(context, false)  // User is not logged out

                    // Fetch user data from Firestore using the UID
                    fetchUserData(authResult.uid)

                    // Also, fetch products as per your existing logic
                    _signInState.value = DataStateAuth.Success(authResult.products)
                },
                onFailure = { error ->
                    _signInState.value = DataStateAuth.Error(error.message ?: "Signin failed")
                }
            )
        }
    }

     fun fetchUserData(uid: String) {
        try {
           viewModelScope.launch {
               // Fetch user data from Firestore using the UID
               val userDoc = repository.getUserData(uid)  // Add this method in your repository
               if (userDoc != null) {
                   _userData.value = userDoc
                   // Save the college and hostel information from the user data
                   SharedPreferencesManager.saveCollege(context, userDoc.college)
                   SharedPreferencesManager.saveHostelNumber(context, userDoc.address?.hostelNumber ?: "")
               } else {
                   _signInState.value = DataStateAuth.Error("User data not found")
               }
           }
        } catch (e: Exception) {
            _signInState.value = DataStateAuth.Error("Failed to fetch user data: ${e.message}")
        }
    }

    fun fetchProductList(college:String,hostel:String){
        viewModelScope.launch {
            val result = repository.fetchProductList(college,hostel)
            if (result.isSuccess) {
                _productList.value = result.getOrNull()?: listOf()
            } else {
                _productList.value = listOf()
            }
        }
    }

    fun updateDetails(
        context: Context,
        updatedName:String,
        updatedPhoneNumber:String,
        updatedHostelNumber:String){
        viewModelScope.launch {
            _updateDetailsState.value=DataState.Idle
            val result = repository.updateUserDetails(
                name = updatedName,
                phoneNumber = updatedPhoneNumber,
                hostelNumber = updatedHostelNumber
            )
            result.fold(
                onSuccess = {
                    _updateDetailsState.value=DataState.Success
                    SharedPreferencesManager.saveHostelNumber(hostelNumber = updatedHostelNumber, context = context )
                    // Notify the user that the update was successful
                    Log.i("ViewModel", "User details updated successfully")
                },
                onFailure = { error ->
                    _updateDetailsState.value=DataState.Error(error.message.toString())
                    // Handle the error (e.g., show a Toast message or update UI)
                    Log.e("ViewModel", "Error updating user details: ${error.message}")
                }
            )
        }
    }
    fun resetChangeCredentialState(){
        _sendEmailState.value=DataState.Idle
        _changePasswordState.value=DataState.Idle
        _verifyEmailState.value=DataState.Idle
    }
    fun resetUpdateState(){
        _updateDetailsState.value=DataState.Idle
    }
    fun resetLogoutState(){
        _logOutState.value=DataState.Idle
    }

    // Check if User is Logged In
    fun isUserLoggedIn(): Boolean {
        val isLoggedOut = SharedPreferencesManager.isLoggedOut(context)
        uid = SharedPreferencesManager.getUid(context)
        return !isLoggedOut && uid != null
    }
    fun resetSignInState() {
        _signInState.value = DataStateAuth.Idle
    }
    fun verifyEmail(){
        viewModelScope.launch {
            _verifyEmailState.value=DataState.Loading
            val result=repository.verifyEmail()
            if (result.isSuccess) {
                _verifyEmailState.value=DataState.Success
                // Notify the user that email verification was successful
            }else{
                _verifyEmailState.value=DataState.Error(result.exceptionOrNull()?.message.toString())
            }
        }
    }
    fun changePassword(currentPassword:String,newPassword:String){
        viewModelScope.launch {
            _changePasswordState.value=DataState.Loading
            val result=repository.changePassword(currentPassword = currentPassword, newPassword = newPassword)
            if (result.isSuccess) {
                _changePasswordState.value=DataState.Success
                // Notify the user that password change was successful
            }else{
                _changePasswordState.value=DataState.Error(result.exceptionOrNull()?.message.toString())
            }
        }
    }




    // LogOut Functionality
    fun logOut() {
        viewModelScope.launch {
            _logOutState.value = DataState.Loading
            val result = repository.logout()
            if (result.isSuccess) {
                _logOutState.value = DataState.Success
                SharedPreferencesManager.removeUid(context)
                resetState() // Reset the ViewModel state on logout
            } else {
                _logOutState.value = DataState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown Error"
                )
            }
        }
    }

    fun forgotPassword(email:String){
        viewModelScope.launch {
            _forgotPasswordState.value=DataState.Loading
            val result=repository.forgotPassword(email = email)
            if (result.isSuccess){
                _forgotPasswordState.value=DataState.Success
            }else{
                _forgotPasswordState.value=DataState.Error(result.exceptionOrNull()?.message.toString())
            }
        }
    }
    fun resetForgotPassword(){
        _forgotPasswordState.value=DataState.Idle
    }
    fun deleteAccount(password: String){
        viewModelScope.launch {
            _deleteAccountState.value=DataState.Loading
            val result=repository.deleteAccount(password = password)
            if (result.isSuccess) {
                _deleteAccountState.value=DataState.Success
            }else{
                _deleteAccountState.value=DataState.Error(result.exceptionOrNull()?.message.toString())
            }
        }
    }
}
