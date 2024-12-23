package com.oxiion.campuscart.domain.screens.adminScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart.common.LoadingDialog
import com.oxiion.campuscart.data.models.roles.Admin
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.ui_app.components.CustomBlackGreenTextBox
import com.oxiion.campuscart.ui_app.components.CustomButton
import com.oxiion.campuscart.utils.LoginState

@Composable
fun AdminSignUpScreen(viewModel: AuthViewModel, onAdminSignUpSuccess:()->Unit){
    val name =remember { mutableStateOf("") }
    val email =remember { mutableStateOf("") }
    val role = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val secretKey = remember { mutableStateOf("") }
    val signUpState by viewModel.signUpState.collectAsState()
    val isLoading = remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }
    Column (
        modifier = Modifier.fillMaxSize().background(
            Color(0xFF000000)
        ).statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Admin",
            fontSize =40.sp,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD8C4A0)
            )
        Text(
            text = "Sign Up",
            fontSize =35.sp,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD8C4A0)
        )
        Spacer(modifier = Modifier.height(32.dp))
        CustomBlackGreenTextBox(
            text = name,
            placeholder = "Enter admin name",
        )
        CustomBlackGreenTextBox(
            text =email,
            placeholder = "Enter email address",
        )
        CustomBlackGreenTextBox(
            text = password,
            placeholder = "Enter password",
        )
        CustomBlackGreenTextBox(
            text = role,
            placeholder = "Enter your role",
        )
        CustomBlackGreenTextBox(
            text=secretKey,
            placeholder = "Enter secret key",
        )
        CustomButton(
            text = "Sign Up",
            onButtonClick = {
                viewModel.signIn(Admin(
                    name = name.value,
                    email = email.value,
                    role = role.value,
                    securityCode = secretKey.value,
                ), password = password)
            }
        )//button
        if (showErrorMessage){
            Text(
                text = signUpState.toString(),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp))
        }
        when(signUpState){
            is LoginState.Error -> {
                isLoading.value=false
                showErrorMessage=true
            }
            LoginState.Idle -> {}
            LoginState.Loading -> {
                isLoading.value = true
                LoadingDialog(isLoading)
            }
            LoginState.Success -> {
                isLoading.value=false
                onAdminSignUpSuccess()
            }
        }
    }
}