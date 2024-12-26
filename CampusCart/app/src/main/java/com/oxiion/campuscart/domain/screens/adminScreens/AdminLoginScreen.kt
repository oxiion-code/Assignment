package com.oxiion.campuscart.domain.screens.adminScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart.common.LoadingDialog
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.ui_app.components.AppTextBox
import com.oxiion.campuscart.utils.StateData
import com.oxiion.campuscart.utils.SharedPreferencesManager


@Composable
private fun ErrorMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(top = 16.dp)
    )
}
@Composable
fun AdminLoginScreen(viewModel: AuthViewModel, onLoginSuccess: () -> Unit, onSignupClick: () -> Unit) {
    val securityCode = remember { mutableStateOf("") }
    val adminEmail = remember { mutableStateOf("") }
    val adminPassword = remember { mutableStateOf("") }
    var chances by remember { mutableIntStateOf(5) }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsState()
    var showErrorMessage by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F1B13)),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier
            .height(26.dp)
            .fillMaxWidth())
        Spacer(modifier = Modifier
            .height(26.dp)
            .fillMaxWidth())
        Text(
            text = "Admin Login",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFF1E7D9),
            fontSize = 29.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier
            .height(32.dp)
            .fillMaxWidth())
       AppTextBox(
           text = securityCode,
           placeholderName = "Enter security code"
       )
        AppTextBox(
            text = adminEmail,
            placeholderName = "Enter admin email"
        )
        OutlinedTextField(
            trailingIcon = {
                IconButton(
                    onClick = {
                        isPasswordVisible.value = !isPasswordVisible.value
                    },
                    modifier = Modifier.size(27.dp)
                ) {
                    val icon: ImageVector =
                        if (isPasswordVisible.value) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp
                    Icon(
                        imageVector = icon,
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            },
            singleLine = true,
            value = adminPassword.value,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.size(width = 300.dp, height = 56.dp),
            visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            onValueChange = { adminPassword.value = it },
            placeholder = {
                Text(
                    text = "Admin Password",
                    color = Color.DarkGray
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.Black,
                focusedBorderColor = Color(0xFFEAC16C),
                unfocusedBorderColor = Color(0xFFFFFFFF),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF1E7D9)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You have $chances chances",
            color = Color(0xFFF7ECDF)
        )
        Spacer(modifier = Modifier.height(32.dp))
        LoadingDialog(isLoading)
        Button(
            enabled = chances > 0,
            onClick = {
                if (chances > 0) {
                    viewModel.login(adminEmail.value, adminPassword.value,securityCode.value)
                    // Authenticate admin
                } else {
                    // Show error message
                    chances--
                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD8C4A0),
                contentColor = Color(0xFF573F00)
            )
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF573F00),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (showErrorMessage) {
            ErrorMessage("Invalid Credentials")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "New Admin ! Click here",
            fontSize = 20.sp,
            fontWeight = FontWeight(500),
            modifier = Modifier.clickable {
                onSignupClick()
            },
            color = Color(0xFFD8C4A0)
        )
        when (loginState) {
            is StateData.Error -> {
                chances--
                isLoading.value = false
                showErrorMessage = true
                Toast.makeText(context, (loginState as StateData.Error).message, Toast.LENGTH_SHORT).show()
                // Reset loginState to prevent repeated toasts
                viewModel.resetLoginState()
            }
            StateData.Idle -> {}
            StateData.Loading -> {
                isLoading.value = true
                LoadingDialog(isLoading)
            }
            is StateData.Success -> {
                chances=5
                SharedPreferencesManager.saveLogOutState(context,false)
                showErrorMessage = false
                isLoading.value = false
                onLoginSuccess()
            }
        }
    }
}
