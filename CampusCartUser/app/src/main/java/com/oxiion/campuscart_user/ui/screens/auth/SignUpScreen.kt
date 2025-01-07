package com.oxiion.campuscart_user.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.oxiion.campuscart_user.data.model.Address
import com.oxiion.campuscart_user.data.model.User
import com.oxiion.campuscart_user.ui.components.AppCustomWhiteButton
import com.oxiion.campuscart_user.ui.components.AppOutlinedTextBox
import com.oxiion.campuscart_user.ui.components.LoadingDialogSmall
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.viewmodels.AuthViewModel
@Composable
fun SignUpScreen(
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel,
    onNextClick: () -> Unit
) {
    val context = LocalContext.current

    // State Variables
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val reEnterPassword = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val isReEnterPasswordVisible = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    val collegeListState by authViewModel.getCollegeListState.collectAsState()

    // Input Validation Function
    fun validateInputs(): Boolean {
        return when {
            email.value.isBlank() -> {
                errorMessage.value = "Email cannot be empty"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches() -> {
                errorMessage.value = "Enter a valid email address"
                false
            }
            password.value.isBlank() -> {
                errorMessage.value = "Password cannot be empty"
                false
            }
            password.value.length < 6 -> {
                errorMessage.value = "Password must be at least 6 characters long"
                false
            }
            reEnterPassword.value != password.value -> {
                errorMessage.value = "Passwords do not match"
                false
            }
            else -> {
                errorMessage.value = ""
                true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFF50606F))
            .imePadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppOutlinedTextBox(
                givenValue = email,
                label = "Email",
                keyboardType = KeyboardType.Email,
                isPassword = false,
                isPasswordVisible = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppOutlinedTextBox(
                givenValue = password,
                label = "Password",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isPasswordVisible = isPasswordVisible
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppOutlinedTextBox(
                givenValue = reEnterPassword,
                label = "Re-enter password",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isPasswordVisible = isReEnterPasswordVisible
            )
            if (errorMessage.value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage.value,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        AppCustomWhiteButton(
            onClick = {
                if (validateInputs()) {
                    if (collegeListState is DataState.Idle) {
                        authViewModel.getCollegeList()
                    }
                    val user= User(
                        college = password.value,
                        address= Address(
                           email = email.value
                        )
                    )
                    authViewModel.saveUserDataBeforeSignUp(user = user)
                    onNextClick()
                }
            },
            text = "Next",
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading.value) {
            LoadingDialogSmall(isLoading)
        }

        // Handle College List State
        when (collegeListState) {
            is DataState.Error -> {
                isLoading.value = false
                Toast.makeText(context, "Unable to fetch college list", Toast.LENGTH_SHORT).show()
            }
            DataState.Idle -> {}
            DataState.Loading -> {
                isLoading.value = true
                LoadingDialogSmall(isLoading)
            }
            is DataState.Success -> {
                isLoading.value = false
            }
        }
    }
}
